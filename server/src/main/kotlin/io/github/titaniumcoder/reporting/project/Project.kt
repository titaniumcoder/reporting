package io.github.titaniumcoder.reporting.project

import org.springframework.data.annotation.Id
import org.springframework.data.annotation.PersistenceConstructor
import org.springframework.data.domain.Persistable
import org.springframework.data.relational.core.mapping.Column
import javax.validation.constraints.Max
import javax.validation.constraints.Min
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Size

data class Project(
        @Id
        @Column("id")
        val projectId: Long? = null,

        @Column("client_id")
        val clientId: String,

        val active: Boolean = true,

        @NotBlank
        @Size(max = 100)
        val name: String,

        // must be bigger than 0 and smaller than 5260320 (10 years)
        @Min(0)
        @Max(5260320)
        val maxMinutes: Int? = null,

        // must be between 0 and 200000
        @Min(0)
        @Max(200000)
        val rateInCentsPerHour: Int? = null,

        val billable: Boolean = true
)

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

data class ProjectDto(
        val id: Long?,

        val clientId: String,
        val clientName: String,

        val active: Boolean,
        val billable: Boolean,

        val name: String,

        val maxMinutes: Int?,

        val rateInCentsPerHour: Int?,

        val minutesUsed: Int,
        val minutesRemaining: Int?,
        val minutesPercentage: Double?,
        val amountBilled: Double,
        val amountOpen: Double,
        val amountRemaining: Double?
)

data class ProjectList(val id: Long?, val clientName: String, val name: String, val billable: Boolean)
