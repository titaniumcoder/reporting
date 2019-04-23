package io.github.titaniumcoder.toggl.reporting.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.web.server.SecurityWebFilterChain

@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
@Configuration
class TogglSecurityConfiguration {
    @Bean
    fun securityWebFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain =
            http
                    .authorizeExchange()
                    .anyExchange().authenticated()
                    .and()
                    .csrf().disable()
                    .httpBasic()
                    .and()
                    .formLogin()
                    .and()
                    .build()
}
