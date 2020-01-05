package io.github.titaniumcoder.reporting

import io.github.titaniumcoder.reporting.config.ReportingConfiguration
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.cache.annotation.EnableCaching
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.web.reactive.config.EnableWebFlux

@SpringBootApplication
@EnableConfigurationProperties(ReportingConfiguration::class)
@EnableCaching
@EnableScheduling
@EnableWebFlux
class Application

fun main(args: Array<String>) {
    runApplication<Application>(*args)
}
