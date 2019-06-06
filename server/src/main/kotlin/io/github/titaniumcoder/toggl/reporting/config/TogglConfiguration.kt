package io.github.titaniumcoder.toggl.reporting.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "application")
data class TogglConfiguration(var apiToken: String = "", var workspaceId: Long = -1L)
