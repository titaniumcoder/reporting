package io.github.titaniumcoder.toggl.reporting.transformers

import io.micronaut.core.convert.format.Format
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZonedDateTime

// Application ViewModel
object ViewModel {
    data class Cashout(
            val client: String,
            val amount: Double
    )

    data class Project(
            val name: String,
            val minutes: Int
    )

    data class TimeEntry(
            val id: Long,
            val day: OffsetDateTime,
            val project: String?,
            val startdate: OffsetDateTime,
            val enddate: OffsetDateTime,
            val minutes: Int,
            val description: String?,
            val tags: List<String>
    )

    data class ReportingModel(
            val client: String,
            val clientId: Long,
            val from: LocalDate,
            val to: LocalDate,
            val projects: List<Project>,
            val timeEntries: List<List<TimeEntry>>
    )
}
