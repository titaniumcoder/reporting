package io.github.titaniumcoder.reporting.project

import javax.validation.constraints.Max
import javax.validation.constraints.Min
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Size

data class ProjectAdminDto(
        val id: Long?,
        val clientId: String,
        val clientName: String?,
        val active: Boolean,
        @NotBlank
        @Size(max = 100)
        val name: String,
        @Min(0)
        @Max(5260320)
        val maxMinutes: Int?,
        @Min(0)
        @Max(200000)
        val rateInCentsPerHour: Int?,
        val billable: Boolean
)
