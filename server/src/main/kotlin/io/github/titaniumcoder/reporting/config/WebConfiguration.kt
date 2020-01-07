package io.github.titaniumcoder.reporting.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.ClassPathResource
import org.springframework.web.reactive.function.server.RouterFunctions.resources

@Configuration
class WebConfiguration {
    @Bean
    fun resRouter() = resources("/**", ClassPathResource("static/"))
}
