package io.github.titaniumcoder.reporting.timeentry

import io.github.titaniumcoder.reporting.client.ClientService
import io.github.titaniumcoder.reporting.model.CurrentTimeEntry
import io.github.titaniumcoder.reporting.model.TimeEntry
import io.github.titaniumcoder.reporting.project.ProjectService
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import javax.inject.Singleton

@Singleton
class TimeEntryService(
        private val repository: TimeEntryRepository,
        private val projectService: ProjectService,
        private val clientService: ClientService
) {
    suspend fun activeTimeEntry(principal: String): TimeEntryDto? =
            toDto(
                    repository.findCurrentEntry()
                            ?: CurrentTimeEntry(
                                    starting = LocalDateTime.now(),
                                    description = null,
                                    billed = false,
                                    projectId = null
                            )
            )

    private fun findById(id: String): TimeEntry? = repository.findById(id)

    // this must be a current time entry!!
    suspend fun startTimeEntry(reference: String?): TimeEntryDto? {
        val ref = reference?.let { findById(it) }

        return repository.save(CurrentTimeEntry(
                starting = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES).plusMinutes(1),
                description = ref?.description,
                billed = false,
                projectId = ref?.projectId
        ))
                .let { toDto(it) }
    }

    suspend fun stopTimeEntry(id: String): TimeEntryDto? {
        val entry = repository.findById(id) ?: return null

        return toDto(repository.save(entry.copy(ending = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES))))
    }

    suspend fun updateTimeEntry(entry: TimeEntryUpdateDto): TimeEntryDto? {
        val oldEntry = repository.findById(entry.id) ?: return null // TODO may be make this a little bit more robus

        return toDto(repository.save(
                oldEntry.copy(
                        billed = entry.billed,
                        description = entry.description,
                        ending = entry.ending,
                        starting = entry.starting
                )))
    }

    private suspend fun toDto(te: TimeEntry): TimeEntryDto? {
        val project = te.projectId?.let { projectService.findProject(it) }
        val client = project?.clientId?.let { clientService.findById(it) }

        val realEnding: LocalDateTime = te.ending

        val finalRate = client?.rateInCentsPerHour ?: 0
        val running: Long = Duration.between(te.starting, realEnding).toMinutes()

        val amount = if (project?.billable != false) running * finalRate / 60.0 / 100.0 else 0.0

        return TimeEntryDto(
                id = te.id.toHexString(),
                starting = te.starting,
                billed = te.billed,
                description = te.description,
                ending = te.ending,

                projectId = project?.id,
                projectName = project?.name,

                date = te.starting.toLocalDate(),

                timeUsed = running,
                amount = amount
        )
    }

    private suspend fun toDto(te: CurrentTimeEntry): TimeEntryDto? {
        val project = te.projectId?.let { projectService.findProject(it) }
        val client = project?.clientId?.let { clientService.findById(it) }

        val realEnding: LocalDateTime = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES)

        val finalRate = client?.rateInCentsPerHour ?: 0
        val running: Long = Duration.between(te.starting, realEnding).toMinutes()

        val amount = if (project?.billable != false) running * finalRate / 60.0 / 100.0 else 0.0

        return TimeEntryDto(
                id = te.id.toHexString(),
                starting = te.starting,
                billed = te.billed,
                description = te.description,
                ending = null,

                projectId = project?.id,
                projectName = project?.name,

                date = te.starting.toLocalDate(),

                timeUsed = running,
                amount = amount
        )
    }

    fun deleteTimeEntry(id: String) {
        findById(id)?.let { repository.delete(it) }
    }

    suspend fun retrieveTimeEntries(from: LocalDate?, to: LocalDate?, clientId: String?, allEntries: Boolean, billableOnly: Boolean): List<TimeEntryDto> {
        return if (allEntries) {
            repository
                    .findAllWithin(from, to, clientId)
                    .mapNotNull { toDto(it) }
        } else {
            repository
                    .findNonBilled(clientId, billableOnly)
                    .mapNotNull { toDto(it) }
        }
    }

    fun togglTimeEntries(ids: List<String>): Int =
            repository.findAllById(ids)
                    .map {
                        it.copy(billed = !it.billed)
                    }
                    .map { repository.save(it) }
                    .count()
}

