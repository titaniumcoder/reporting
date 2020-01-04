package io.github.titaniumcoder.reporting.transformers

import com.fasterxml.jackson.annotation.JsonFormat
import io.github.titaniumcoder.reporting.timeentry.TimeEntry
import java.time.LocalDate

object ViewModel {
    data class Project(
            val name: String,

            val minutesTotal: Int?,
            val minutesWorked: Int,

            val percentage: Double?
    )

    data class ReportingModel(
            val client: String,
            val clientId: Long,
            @JsonFormat(pattern = "yyyy-MM-dd")
            val from: LocalDate,
            @JsonFormat(pattern = "yyyy-MM-dd")
            val to: LocalDate,
            val projects: List<Project>,
            val timeEntries: List<List<TimeEntry>>
    )
}
