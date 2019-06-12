package io.github.titaniumcoder.toggl.reporting.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "application", ignoreInvalidFields = false, ignoreUnknownFields = false)
data class TogglConfiguration(
        var apiToken: String = "",
        var workspaceId: Long = -1L,
        var username: String = "",
        var password: String = "",
        var security: SecurityEncodingConfiguration = SecurityEncodingConfiguration()
)

data class SecurityEncodingConfiguration(var secret: String = "", var iteration: Int = 1, var keylength: Int = 64, var expiration: Long = -1L)
