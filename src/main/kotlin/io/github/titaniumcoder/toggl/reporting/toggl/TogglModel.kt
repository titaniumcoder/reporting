package io.github.titaniumcoder.toggl.reporting.toggl

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDateTime

// -----------  Toggl Models -----------------
object TogglModel {
    data class Client(
            val id: Long,
            val wid: Long,
            val name: String,
            val notes: String?
    )

    data class TogglCurrency(
            val currency: String?,
            val amount: Double?
    )

    data class TimeEntryReporting(
            val id: Long,
            val pid: Long?,
            val project: String?,
            val client: String?,
            val task: String?,
            val description: String?,
            val start: LocalDateTime,
            @JsonProperty("end")
            val end: LocalDateTime,
            @JsonProperty("dur")
            val duration: Int, // duration ms
            val billable: Float,
            @JsonProperty("is_billable")
            val isBillable: Boolean,
            @JsonProperty("cur")
            val currency: String, // currency
            val tags: List<String>
    )

    data class TogglReporting(
            @JsonProperty("total_count")
            val totalCount: Int,
            @JsonProperty("per_page")
            val perPage: Int,
            @JsonProperty("total_grand")
            val totalGrand: Long?,
            @JsonProperty("total_billable")
            val totalBillable: Long?,
            @JsonProperty("total_currencies")
            val totalCurrencies: List<TogglCurrency>,
            val data: List<TimeEntryReporting>
    )

    data class ClientSummaryEntry(
            val title: TogglIClient?,
            val time: Long,
            @JsonProperty("total_currencies")
            val totalCurrencies: List<TogglCurrency>
    )

    data class ProjectSummaryEntry(
            val title: TogglIClient?,
            val time: Long,
            @JsonProperty("cur")
            val currency: String?,
            val sum: Double?,
            val rate: Double?
    )

    data class TogglIClient(
            @JsonProperty("client")
            val name: String?
    )

    data class TogglSummary(
            @JsonProperty("total_grand")
            val totalGrand: Long?,
            @JsonProperty("total_billable")
            val totalBillable: Long?,
            @JsonProperty("total_currencies")
            val totalCurrencies: List<TogglCurrency>,
            val data: List<ClientSummaryEntry>
    )

}