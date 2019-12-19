package io.github.titaniumcoder.toggl.reporting.transformers

import com.fasterxml.jackson.annotation.JsonFormat
import java.time.LocalDate
import java.time.OffsetDateTime

// Application ViewModel
object ViewModel {
    data class HeaderInfo(
            val cashouts: List<Cashout>,
            val projectLimits: List<ProjectLimit>,
            val clientLimits: List<ClientLimit>,
            val totalCashout: Double
    )

    data class ProjectLimit(
            val id: Long,
            val project: String,
            val year: Int,
            val maxHours: Double,
            val totalHoursBilled: Double,
            val totalHoursOpen: Double,
            val totalHoursUsed: Double,
            val percentage: Double
    )

    data class ClientLimit(
            val id: Long,
            val client: String,
            val year: Int,
            val maxHours: Double,
            val totalHoursBilled: Double,
            val totalHoursOpen: Double,
            val totalHoursUsed: Double,
            val percentage: Double
    )

    data class Cashout(
            val client: String,
            val amount: Double
    )

    data class Project(
            val name: String,
            val minutesInRange: Int,


            val minutesTotal: Int,
            val minutesBilled: Int,
            val minutesOpen: Int,

            val percentage: Double?
    )

    data class TimeEntry(
            val id: Long,
            @JsonFormat(pattern = "yyyy-MM-dd", locale = "Europe/Zurich")
            val day: OffsetDateTime,
            val project: String?,
            @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss", locale = "Europe/Zurich")
            val startdate: OffsetDateTime,
            @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss", locale = "Europe/Zurich")
            val enddate: OffsetDateTime,
            val minutes: Int,
            val description: String?,
            val tags: List<String>
    )

    data class ReportingModel(
            val client: String,
            val clientId: Long,
            @JsonFormat(pattern = "yyyy-MM-dd", locale = "Europe/Zurich")
            val from: LocalDate,
            @JsonFormat(pattern = "yyyy-MM-dd", locale = "Europe/Zurich")
            val to: LocalDate,
            val projects: List<Project>,
            val timeEntries: List<List<TimeEntry>>
    )
}
