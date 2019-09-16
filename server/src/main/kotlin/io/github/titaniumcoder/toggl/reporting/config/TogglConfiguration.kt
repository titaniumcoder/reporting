package io.github.titaniumcoder.toggl.reporting.config

import io.micronaut.context.annotation.ConfigurationProperties
import javax.validation.constraints.NotBlank

@ConfigurationProperties("application")
class TogglConfiguration {
    @NotBlank
    lateinit var apiToken: String

    @NotBlank
    var workspaceId: Long = -1L

    lateinit var security: SecurityEncodingConfiguration

    @ConfigurationProperties("security")
    class SecurityEncodingConfiguration {
        @NotBlank
        lateinit var secret: String

        @NotBlank
        lateinit var username: String

        @NotBlank
        lateinit var password: String

        var expiration: Long = -1L
    }
}

