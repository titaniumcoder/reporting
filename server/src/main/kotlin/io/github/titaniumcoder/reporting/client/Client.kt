package io.github.titaniumcoder.reporting.client

import org.springframework.data.annotation.Id
import org.springframework.data.annotation.PersistenceConstructor
import org.springframework.data.annotation.Transient
import org.springframework.data.domain.Persistable
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import javax.validation.constraints.Max
import javax.validation.constraints.Min
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Size

@Table("client")
data class Client(
        @NotBlank
        @Id
        @Column("id")
        val clientId: String,

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
        @Column("max_minutes")
        val maxMinutes: Int? = null,

        // must be between 0 and 200000
        @Min(0)
        @Max(200000)
        @Column("rate_in_cents_per_hour")
        val rateInCentsPerHour: Int? = null,

        @Transient val newClient: Boolean = true
) : Persistable<String> {
    override fun getId(): String? = clientId

    override fun isNew(): Boolean = newClient

    @PersistenceConstructor
    constructor(clientId: String, active: Boolean, name: String, notes: String?, maxMinutes: Int?, rateInCentsPerHour: Int?) :
            this(clientId, active, name, notes, maxMinutes, rateInCentsPerHour, false)
}

data class ClientUpdatingDto(
        @NotBlank
        val clientId: String,

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

data class ClientListDto(
        val clientId: String,
        val name: String
)
