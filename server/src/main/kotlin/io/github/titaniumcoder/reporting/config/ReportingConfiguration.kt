package io.github.titaniumcoder.reporting.config

import io.micronaut.context.annotation.ConfigurationProperties

@ConfigurationProperties("reporting")
data class ReportingConfiguration(
        val clientId: String,
        val adminEmail: String
)
