package io.github.titaniumcoder.reporting.timeentry

import io.github.titaniumcoder.reporting.project.ProjectService
import io.github.titaniumcoder.reporting.user.UserDto
import io.github.titaniumcoder.reporting.user.UserService
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import javax.inject.Singleton

@Singleton
class TimeEntryService(
        private val repository: TimeEntryRepository,
        private val projectService: ProjectService,
        private val userService: UserService
) {
    fun activeTimeEntry(principal: String) =
            userService.findByEmail(principal)
                    ?.let { userService.toDto(it) }
                    ?.let { u ->
                        Pair(repository.findLastOpenEntry(u.email)
                                ?: TimeEntry(
                                        id = null,
                                        starting = LocalDateTime.now(),
                                        ending = null,
                                        projectId = null,
                                        description = null,
                                        email = u.email,
                                        billed = false), u)
                    }
                    ?.let { toDto(it.first, it.second) }

    fun findById(id: Long): TimeEntry? {
        val te = repository.findById(id)

        val user = userService.reactiveCurrentUserDto()!! // TODO make sure this works

        return when {
            te == null -> null
            user.admin -> te
            user.canBook && te.projectId == null -> te
            user.canBook && te.projectId != null ->
                if (projectService.findProject(te.projectId, user)?.let {
                            userService.userHasAccessToProject(user, it)
                        } == true) te else null
            else -> null
        }
    }

    fun startTimeEntry(reference: Long?): TimeEntryDto? {
        val ref = reference?.let { findById(it) }

        val user = userService.reactiveCurrentUserDto() ?: return null

        return repository.save(TimeEntry(
                id = null,
                starting = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES).plusMinutes(1),
                ending = null,
                projectId = ref?.projectId,
                description = ref?.description,
                email = user.email,
                billed = false
        ))
                .let { toDto(it, user) }
    }

    fun stopTimeEntry(id: Long): TimeEntryDto? {
        val entry = repository.findById(id) ?: return null
        val user = userService.reactiveCurrentUserDto() ?: return null

        return toDto(repository.save(entry.copy(ending = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES))), user)
    }

    fun updateTimeEntry(entry: TimeEntryUpdateDto): TimeEntryDto? {
        val oldEntry = repository.findById(entry.id) ?: return null // TODO may be make this a little bit more robus

        val user = userService.reactiveCurrentUserDto() ?: return null

        return toDto(repository.save(
                oldEntry.copy(
                        projectId = entry.projectId,
                        billed = entry.billed,
                        description = entry.description,
                        ending = entry.ending,
                        starting = entry.starting,
                        email = user.email
                )), user)
    }

    private fun toDto(te: TimeEntry, userDto: UserDto): TimeEntryDto? {
        val project = te.projectId?.let { projectService.findProjectAdminDto(it, userDto) }
        val client = te.projectId?.let { projectService.findClientForProject(it, userDto) }

        val realEnding: LocalDateTime = te.ending ?: LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES)

        val projectRate = project?.rateInCentsPerHour?.let { if (it == 0) null else it }
        val clientRate = client?.rateInCentsPerHour

        val finalRate = projectRate ?: clientRate ?: 0
        val running: Long = Duration.between(te.starting, realEnding).toMinutes()

        val amount = if (project?.billable != false) running * finalRate / 60.0 / 100.0 else 0.0

        return TimeEntryDto(
                id = te.id,
                starting = te.starting,
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

    fun deleteTimeEntry(id: Long) {
        findById(id)?.let { repository.delete(it) }
    }

    fun retrieveTimeEntries(from: LocalDate?, to: LocalDate?, clientId: String?, allEntries: Boolean, billableOnly: Boolean): List<TimeEntryDto> {
        val user = userService.reactiveCurrentUserDto()!!
        return if (allEntries) {
            repository
                    .findAllWithin(from, to, clientId, if (user.admin) null else user.email)
                    .mapNotNull { toDto(it, user) }
        } else {
            repository
                    .findNonBilled(clientId, billableOnly, if (user.admin) null else user.email)
                    .mapNotNull { toDto(it, user) }
        }
    }

    fun togglTimeEntries(ids: List<Long>): Int =
            repository.findAllById(ids)
                    .map {
                        it.copy(billed = !it.billed)
                    }
                    .map { repository.save(it) }
                    .count()
}

