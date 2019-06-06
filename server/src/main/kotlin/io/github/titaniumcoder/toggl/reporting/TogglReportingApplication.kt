package io.github.titaniumcoder.toggl.reporting

import io.github.titaniumcoder.toggl.reporting.config.TogglConfiguration
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication

@SpringBootApplication
@EnableConfigurationProperties(TogglConfiguration::class)
class TogglReportingApplication

fun main(args: Array<String>) {
    runApplication<TogglReportingApplication>(*args)
}
