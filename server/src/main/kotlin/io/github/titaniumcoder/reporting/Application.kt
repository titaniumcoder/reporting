package io.github.titaniumcoder.reporting

import io.github.titaniumcoder.reporting.config.ReportingConfiguration
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication

@SpringBootApplication
@EnableConfigurationProperties(ReportingConfiguration::class)
class ServerSpringApplication

fun main(args: Array<String>) {
    runApplication<ServerSpringApplication>(*args)
}
