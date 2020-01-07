package io.github.titaniumcoder.reporting.timeentry

import io.github.titaniumcoder.reporting.client.Client
import io.github.titaniumcoder.reporting.project.ProjectAdminDto
import io.github.titaniumcoder.reporting.project.ProjectService
import io.github.titaniumcoder.reporting.user.UserDto
import io.github.titaniumcoder.reporting.user.UserService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Mono
import reactor.util.function.Tuple2
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import java.util.*

@Service
@Transactional
class TimeEntryService(val repository: TimeEntryRepository, val projectService: ProjectService, val userService: UserService) {
    fun activeTimeEntry(principal: String) =
            userService.findByEmail(principal)
                    .flatMap { userService.toDto(it) }
                    .flatMap { u -> repository.findLastOpenEntry(u.email).map { Pair(it, u) } }
                    .flatMap { toDto(it.first, it.second) }

    fun findById(id: Long): Mono<TimeEntry> {
        val monoTe = repository.findById(id)

        val monoUser = userService.reactiveCurrentUserDto()

        return monoTe
                .zipWith(monoUser)
                .flatMap { tuple ->
                    val te = tuple.t1
                    val user = tuple.t2

                    when {
                        user.admin -> Mono.just(te)
                        user.canBook && te.projectId == null -> Mono.just(te)
                        user.canBook && te.projectId != null ->
                            projectService.findProject(te.projectId, user).map {
                                userService.userHasAccessToProject(user, it)
                            }
                                    .filter { true }
                                    .map { te }
                        else -> Mono.empty()
                    }
                }
    }

    fun startTimeEntry(reference: Long?): Mono<TimeEntryDto> {
        val referencedTimeEntry = Mono.justOrEmpty(reference)
                .flatMap { findById(it) }
                .map { Optional.of(it) }
                .switchIfEmpty(Mono.just(Optional.empty<TimeEntry>()))

        val currentUser = userService.reactiveCurrentUserDto()

        return referencedTimeEntry
                .zipWith(currentUser)
                .flatMap { t ->
                    val user = t.t2
                    val ref = t.t1

                    repository.save(TimeEntry(
                            id = null,
                            starting = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES).plusMinutes(1),
                            ending = null,
                            projectId = ref.map { it.projectId }.orElse(null),
                            description = ref.map { it.description }.orElse(null),
                            email = user.email,
                            billable = ref.map { it.billable }.orElse(true),
                            billed = false
                    ))
                            .flatMap { toDto(it, user) }
                }
    }

    fun stopTimeEntry(id: Long): Mono<TimeEntryDto> {
        val entry = repository.findById(id)

        val user = userService.reactiveCurrentUserDto()

        return entry
                .zipWith(user)
                .flatMap { t ->
                    repository.save(t.t1.copy(ending = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES)))
                            .flatMap { toDto(it, t.t2) }
                }
    }

    fun updateTimeEntry(entry: TimeEntryUpdateDto): Mono<TimeEntryDto> {
        val oldEntryMono = repository.findById(entry.id) // TODO may be make this a little bit more robus

        val userMono = userService.reactiveCurrentUserDto()

        return oldEntryMono
                .zipWith(userMono)
                .flatMap { t ->
                    val oldEntry = t.t1
                    val user = t.t2

                    repository.save(
                            oldEntry.copy(
                                    projectId = entry.projectId,
                                    billable = entry.billable,
                                    billed = entry.billed,
                                    description = entry.description,
                                    ending = entry.ending,
                                    starting = entry.starting,
                                    email = user.email
                            )
                    ).flatMap { toDto(it, user) }
                }
    }

    private fun toDto(te: TimeEntry, userDto: UserDto): Mono<TimeEntryDto> {
        val projectClientMono = Mono.justOrEmpty(te.projectId)
                .flatMap { projectService.findProjectAdminDto(it, userDto).zipWith(projectService.findClientForProject(it, userDto)) }
                .map { Optional.of(it) }
                .switchIfEmpty(Mono.just(Optional.empty()))

        val realEnding: LocalDateTime = te.ending ?: LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES)

        return projectClientMono.map { p ->
            val projectClient: Tuple2<ProjectAdminDto, Client>? = p.orElse(null)

            val project = projectClient?.t1
            val client = projectClient?.t2

            val projectRate = project?.rateInCentsPerHour?.let { if (it == 0) null else it }
            val clientRate = client?.rateInCentsPerHour

            val finalRate = projectRate ?: clientRate ?: 0
            val running: Long = Duration.between(te.starting, realEnding).toMinutes()

            val amount = if (te.billable) running * finalRate  / 60.0 / 100.0 else 0.0

            TimeEntryDto(
                    id = te.id,
                    starting = te.starting,
                    billable = te.billable,
                    billed = te.billed,
                    description = te.description,
                    ending = te.ending,

                    projectId = project?.id,
                    projectName = project?.name,

                    username = te.email,

                    date = te.starting.toLocalDate(),

                    timeUsed = running,
                    amount = amount
            )
        }
    }

    fun deleteTimeEntry(id: Long) =
            findById(id)
                    .flatMap { repository.delete(it) }

    fun retrieveTimeEntries(from: LocalDate?, to: LocalDate?, clientId: String?, allEntries: Boolean) =
            userService.reactiveCurrentUserDto().flux()
                    .flatMap { user ->
                        if (allEntries) {
                            repository.findAllWithin(from, to, clientId)
                                    .flatMap { toDto(it, user) }
                        } else {
                            repository.findNonBilled(clientId)
                                    .flatMap { toDto(it, user) }
                        }
                    }

    fun togglTimeEntries(ids: List<Long>): Mono<Long> =
            repository.findAllById(ids)
                    .map {
                        it.copy(billed = !it.billed)
                    }
                    .flatMap { repository.save(it) }
                    .count()
}

