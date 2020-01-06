package io.github.titaniumcoder.reporting.timeentry

import io.github.titaniumcoder.reporting.client.Client
import io.github.titaniumcoder.reporting.project.ProjectAdminDto
import io.github.titaniumcoder.reporting.project.ProjectService
import io.github.titaniumcoder.reporting.user.UserService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Mono
import reactor.util.function.Tuple2
import java.time.Duration
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import java.util.*

@Service
@Transactional
class TimeEntryService(val repository: TimeEntryRepository, val projectService: ProjectService, val userService: UserService) {
    fun activeTimeEntry(principal: String) =
            userService.findByEmail(principal)
                    .flatMap { u ->
                        repository.findLastOpenEntry(u.email)
                    }
                    .flatMap { toDto(it) }

    fun findById(id: Long): Mono<TimeEntry> {
        val monoTe = repository.findById(id)

        val monoUser = userService.reactiveCurrentUser()

        return monoTe
                .zipWith(monoUser)
                .flatMap { tuple ->
                    val te = tuple.t1
                    val user = tuple.t2

                    when {
                        user.admin -> Mono.just(te)
                        user.canBook && te.projectId == null -> Mono.just(te)
                        user.canBook && te.projectId != null ->
                            projectService.findProject(te.projectId).flatMap {
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

        val currentUser = userService.reactiveCurrentUser()

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
                }
                .flatMap { toDto(it) }
    }

    fun stopTimeEntry(id: Long): Mono<TimeEntryDto> {
        val entry = repository.findById(id)

        return entry.flatMap {
            repository.save(it.copy(ending = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES)))
        }
                .flatMap { toDto(it) }
    }

    fun updateTimeEntry(entry: TimeEntryUpdateDto): Mono<TimeEntryDto> {
        val oldEntryMono = repository.findById(entry.id) // TODO may be make this a little bit more robus

        val userMono = userService.reactiveCurrentUser()

        return oldEntryMono
                .zipWith(userMono)
                .map { t ->
                    val oldEntry = t.t1
                    val user = t.t2

                    oldEntry.copy(
                            projectId = entry.projectId,
                            billable = entry.billable,
                            billed = entry.billed,
                            description = entry.description,
                            ending = entry.ending,
                            starting = entry.starting,
                            email = user.email
                    )
                }
                .flatMap { repository.save(it) }
                .flatMap { toDto(it) }
    }

    private fun toDto(te: TimeEntry): Mono<TimeEntryDto> {
        val projectClientMono = Mono.justOrEmpty(te.projectId)
                .flatMap { projectService.findProjectAdminDto(it).zipWith(projectService.findClientForProject(it)) }
                .map { Optional.of(it) }
                .switchIfEmpty(Mono.just(Optional.empty()))

        val realEnding: LocalDateTime = te.ending ?: LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES)

        return projectClientMono.map { p ->
            val projectClient: Tuple2<ProjectAdminDto, Client>? = p.orElse(null)

            val project = projectClient?.t1
            val client = projectClient?.t2

            val projectRate = project?.rateInCentsPerHour
            val clientRate = client?.rateInCentsPerHour

            val finalRate = projectRate ?: clientRate ?: 0
            val running: Long = Duration.between(te.starting, realEnding).toMinutes()

            val amount = (running / 60.0) * finalRate / 100.0

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

    fun retrieveTimeEntries(from: LocalDateTime?, to: LocalDateTime?, clientId: String?, allEntries: Boolean) =
        repository.findAllWithin(from, to, clientId, allEntries)
                .flatMap { toDto(it) }

    fun togglTimeEntries(ids: List<Long>): Mono<Long> =
        repository.findAllById(ids)
                .map {
                    it.copy(billed = !it.billed)
                }
                .map { repository.save(it) }
                .count()
}

