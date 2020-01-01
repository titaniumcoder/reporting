package io.github.titaniumcoder.reporting.client

import io.github.titaniumcoder.reporting.project.ProjectDto
import io.github.titaniumcoder.reporting.timeentry.TimeEntryDto
import javax.validation.constraints.Max
import javax.validation.constraints.Min
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Size

data class Client(
        // Unique, 10 chars, not blank
        @NotBlank
        val id: String,

        val active: Boolean = true,

        // max 100 chars, not blank
        @NotBlank
        @Size(max = 100)
        val name: String,

        // can be whatever there is, max 255 chars
        @Size(max = 255)
        val notes: String? = null,

        // must be bigger than 0 and smaller than 5260320 (10 years)
        @Min(0)
        @Max(5260320)
        val maxMinutes: Int? = null,

        // must be between 0 and 200000
        @Min(0)
        @Max(200000)
        val rateInCentsPerHour: Int? = null
)

data class ClientDto(
        val client: Client,
        val projects: List<ProjectDto>,
        val minutesUsed: Int,
        val minutesRemaining: Int?,
        val minutesPercentage: Double?,
        val amountBilled: Double,
        val amountOpen: Double,
        val amountRemaining: Double?,
        val timeEntries: List<TimeEntryDto> = listOf()
)
