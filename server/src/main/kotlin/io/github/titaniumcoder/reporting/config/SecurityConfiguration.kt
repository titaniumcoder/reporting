package io.github.titaniumcoder.reporting.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.security.web.server.context.ServerSecurityContextRepository
import reactor.core.publisher.Mono


@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
class SecurityConfiguration(val securityContextRepository: ServerSecurityContextRepository, val authenticationManager: ReactiveAuthenticationManager) {

    @Bean
    fun securitygWebFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain? {
        return http
                .cors().disable()
                .csrf().disable()
                .formLogin().disable()
                .httpBasic().disable()
                .exceptionHandling()
                .authenticationEntryPoint { swe, _ ->
                    Mono.fromRunnable {
                        swe.response.statusCode = HttpStatus.UNAUTHORIZED
                    }
                }
                .accessDeniedHandler { swe, _ ->
                    Mono.fromRunnable {
                        swe.response.statusCode = HttpStatus.FORBIDDEN
                    }
                }
                .and()
                .securityContextRepository(securityContextRepository)
                .authenticationManager(authenticationManager)
                .authorizeExchange()
                .pathMatchers(HttpMethod.OPTIONS).permitAll()
                .pathMatchers("/", "/index.html", "/*.json", "/favicon.ico", "/*.js", "/static/**", "/robots.txt", "/error", "/sse/**").permitAll()
                .anyExchange().authenticated()
                .and().build()
    }
}

