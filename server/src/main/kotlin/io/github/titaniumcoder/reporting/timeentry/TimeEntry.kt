package io.github.titaniumcoder.reporting.timeentry

import com.fasterxml.jackson.annotation.JsonFormat
import org.springframework.data.annotation.Id
import java.time.LocalDate
import java.time.LocalDateTime
import javax.validation.constraints.Size

data class TimeEntry(
        @Id
        var id: Long?,

        var starting: LocalDateTime,

        var ending: LocalDateTime?,

        val projectId: Long?,

        @Size(max = 200)
        var description: String?,

        val email: String,

        var billable: Boolean = true,
        var billed: Boolean = false
)

data class TimeEntryDto(
        val id: Long?,

        @JsonFormat(pattern = "yyyy-MM-dd")
        val date: LocalDate,

        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm")
        val starting: LocalDateTime,
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm")
        val ending: LocalDateTime?,

        val projectId: Long?,
        val projectName: String?,

        @Size(max = 200)
        val description: String?,

        val username: String,

        val billable: Boolean,
        val billed: Boolean,

        val timeUsed: Long?,
        val amount: Double?
)

data class TimeEntryUpdateDto(
        val id: Long,

        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm")
        val starting: LocalDateTime,
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm")
        val ending: LocalDateTime?,

        val projectId: Long?,

        @Size(max = 200)
        val description: String?,

        val username: String,

        val billable: Boolean,
        val billed: Boolean
)
