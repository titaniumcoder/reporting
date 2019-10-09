package io.github.titaniumcoder.toggl.reporting.config

import org.springframework.context.annotation.Bean
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus.FORBIDDEN
import org.springframework.http.HttpStatus.UNAUTHORIZED
import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextImpl
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService
import org.springframework.security.core.userdetails.User
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.security.web.server.context.ServerSecurityContextRepository
import org.springframework.stereotype.Component
import org.springframework.web.reactive.config.EnableWebFlux
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
class WebfluxSecurityConfiguration(
        val authenticationManager: ReactiveAuthenticationManager,
        val securityContextRepository: SecurityContextRepository
) {
    @Bean
    fun userDetailsService(): MapReactiveUserDetailsService {
        val user = User
                .withUsername("test") // TODO config
                .password("test")
                .roles("USER")
                .build()
        return MapReactiveUserDetailsService(user)
    }

    @Bean
    fun springSecurityFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain {
        http
                .csrf().disable()
                .exceptionHandling()
                .authenticationEntryPoint { swe, _ ->
                    Mono.fromRunnable {
                        swe.response.statusCode = UNAUTHORIZED
                    }
                }.accessDeniedHandler { swe, _ ->
                    Mono.fromRunnable {
                        swe.response.statusCode = FORBIDDEN
                    }
                }
                .and()
                .authorizeExchange()
                .pathMatchers(HttpMethod.OPTIONS).permitAll()
                .pathMatchers(HttpMethod.POST, "/api/login").permitAll()
                .pathMatchers(HttpMethod.POST, "/api/health").permitAll()
                .pathMatchers("/api/**").authenticated()
                .pathMatchers("/**").permitAll()
                .and()
                .formLogin().disable()
                .httpBasic().disable()
                .authenticationManager(authenticationManager)
                .securityContextRepository(securityContextRepository)
        return http.build();
    }
}


@Component
class SecurityContextRepository(val authenticationManager: ReactiveAuthenticationManager) : ServerSecurityContextRepository {
    override fun save(swe: ServerWebExchange, sc: SecurityContext): Mono<Void> {
        throw UnsupportedOperationException("Not supported yet.")
    }

    override fun load(swe: ServerWebExchange): Mono<SecurityContext> {
        val request = swe.request
        val authHeader = request.headers.getFirst(HttpHeaders.AUTHORIZATION)

        val headerName = "Bearer "

        return if (authHeader != null && authHeader.startsWith(headerName)) {
            val authToken = authHeader.substring(headerName.length)
            val auth = UsernamePasswordAuthenticationToken(authToken, authToken)
            authenticationManager.authenticate(auth).map { SecurityContextImpl(it) }
        } else {
            Mono.empty()
        }
    }
}
