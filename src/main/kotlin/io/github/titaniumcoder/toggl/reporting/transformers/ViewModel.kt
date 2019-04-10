package io.github.titaniumcoder.toggl.reporting.transformers

import java.time.LocalDate
import java.time.LocalDateTime

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
            val day: LocalDate,
            val project: String?,
            val startdate: LocalDateTime,
            val enddate: LocalDateTime?,
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
