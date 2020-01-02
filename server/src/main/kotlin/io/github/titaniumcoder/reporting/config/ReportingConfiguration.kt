package io.github.titaniumcoder.reporting.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConstructorBinding
@ConfigurationProperties(prefix = "reporting")
data class ReportingConfiguration(
        val clientId: String,
        val clientSecret: String,
        val adminEmail: String
)
