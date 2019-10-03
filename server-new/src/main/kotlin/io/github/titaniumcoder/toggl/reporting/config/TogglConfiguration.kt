package io.github.titaniumcoder.toggl.reporting.config

import org.springframework.boot.context.properties.ConfigurationProperties
import javax.validation.constraints.NotBlank

@ConfigurationProperties(prefix = "application")
class TogglConfiguration {
    @NotBlank
    lateinit var apiToken: String

    @NotBlank
    var workspaceId: Long = -1L

    var security: SecurityEncodingConfiguration = SecurityEncodingConfiguration()

    class SecurityEncodingConfiguration {
        @NotBlank
        lateinit var username: String

        @NotBlank
        lateinit var password: String
    }
}

