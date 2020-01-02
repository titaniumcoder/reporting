package io.github.titaniumcoder.reporting

import io.github.titaniumcoder.reporting.config.ReportingConfiguration
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.cache.annotation.EnableCaching

@SpringBootApplication
@EnableConfigurationProperties(ReportingConfiguration::class)
@EnableCaching
class ServerSpringApplication

fun main(args: Array<String>) {
    runApplication<ServerSpringApplication>(*args)
}
