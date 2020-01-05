package io.github.titaniumcoder.reporting.timeentry

import io.github.titaniumcoder.reporting.project.ProjectService
import io.github.titaniumcoder.reporting.user.UserService
import org.springframework.data.domain.PageRequest
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Duration
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

@Service
@Transactional
class TimeEntryService(val repository: TimeEntryRepository, val projectService: ProjectService, val userService: UserService) {
    fun activeTimeEntry(principal: String): TimeEntryDto? {
        val user = userService.findByEmail(principal) ?: throw IllegalArgumentException("Unknown user: $principal")

        return repository.findLastOpenEntry(user.email, PageRequest.of(0, 1)).firstOrNull()?.let { toDto(it) }
    }

    fun readTimeEntry(id: Long): TimeEntry? {
        val te = repository.findByIdOrNull(id) ?: throw IllegalArgumentException("could not load entry with id $id")

        val user = userService.currentUser()

        return when {
            user.admin -> te
            user.canBook && te.project == null -> te
            user.canBook && te.project != null && userService.userHasAccessToProject(user, te.project) -> te
            else -> null
        }
    }

    fun startTimeEntry(reference: Long?): TimeEntryDto {
        val ref = reference?.let { readTimeEntry(it) }

        val te = repository.save(TimeEntry(
                id = null,
                starting = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES).plusMinutes(1),
                ending = null,
                project = ref?.project,
                description = ref?.description,
                user = userService.currentUser(),
                billable = ref?.project?.billable ?: true,
                billed = false
        ))

        return toDto(te)
    }

    fun stopTimeEntry(entry: TimeEntryUpdateDto): TimeEntryDto {
        val endingEntry = entry.copy(
                ending = entry.ending ?: LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES)
        )
        return updateTimeEntry(endingEntry)
    }

    fun updateTimeEntry(entry: TimeEntryUpdateDto): TimeEntryDto {
        val oldEntry = repository.findByIdOrNull(entry.id)
        val te = oldEntry?.copy(
                project = entry.projectId?.let { projectService.findProject(it) },
                billable = entry.billable,
                billed = entry.billed,
                description = entry.description,
                ending = entry.ending,
                starting = entry.starting,
                user = userService.currentUser()
        )
                ?: TimeEntry(
                        project = entry.projectId?.let { projectService.findProject(it) },
                        billable = entry.billable,
                        billed = entry.billed,
                        description = entry.description,
                        ending = entry.ending,
                        starting = entry.starting,
                        user = userService.currentUser(),
                        id = null
                )

        return toDto(repository.save(te))
    }

    private fun toDto(te: TimeEntry): TimeEntryDto {
        val realEnding: LocalDateTime = te.ending ?: LocalDateTime.now()

        val projectRate = te.project?.rateInCentsPerHour
        val clientRate = te.project?.client?.rateInCentsPerHour

        val finalRate = projectRate ?: clientRate ?: 0
        val running: Long = Duration.between(te.starting, realEnding).toMinutes()

        val amount = (running / 60.0) * finalRate / 100.0

        return TimeEntryDto(
                id = te.id,
                starting = te.starting,
                billable = te.billable,
                billed = te.billed,
                description = te.description,
                ending = te.ending,

                projectId = te.project?.id,
                projectName = te.project?.name,

                username = te.user.email,

                date = te.starting.toLocalDate(),

                timeUsed = running,
                amount = amount
        )
    }
}

