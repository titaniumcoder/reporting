package io.github.titaniumcoder.toggl.reporting.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextImpl
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService
import org.springframework.security.core.userdetails.User
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.security.web.server.context.ServerSecurityContextRepository
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
@Configuration
class TogglSecurityConfiguration(private val configuration: TogglConfiguration) {

    @Bean
    fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()

    @Bean
    fun securityWebFilterChain(
            http: ServerHttpSecurity,
            authenticationManager:
            ReactiveAuthenticationManager,
            securityContextRepository: ServerSecurityContextRepository
    ): SecurityWebFilterChain =
            http
                    .authorizeExchange()
                    .anyExchange().permitAll()

                    .and()

                    .exceptionHandling()
                    .authenticationEntryPoint { exchange, _ ->
                        Mono.fromRunnable<Void> {
                            exchange.response.statusCode = HttpStatus.UNAUTHORIZED
                        }
                    }
                    .accessDeniedHandler { exchange, _ ->
                        Mono.fromRunnable<Void> {
                            exchange.response.statusCode = HttpStatus.UNAUTHORIZED
                        }
                    }

                    .and()

                    .csrf().disable()
                    .httpBasic().disable()
                    .formLogin().disable()
                    .logout().disable()

                    .authenticationManager(authenticationManager)
                    .securityContextRepository(securityContextRepository)
                    .build()

    @Bean
    fun userDetailsService(): MapReactiveUserDetailsService {
        val user = User
                .withUsername(configuration.security.username)
                .password(configuration.security.password)
                .roles("USER")
                .build()
        return MapReactiveUserDetailsService(user)
    }

    @Bean
    fun authenticationManager(jwtUtil: JWTUtil): ReactiveAuthenticationManager =
            ReactiveAuthenticationManager { authentication ->
                val authToken = authentication.credentials.toString()

                val username: String? =
                        try {
                            jwtUtil.getUsernameFromToken(authToken)
                        } catch (e: Exception) {
                            null
                        }

                if (username != null && jwtUtil.validateToken(authToken)) {
                    val claims = jwtUtil.getAllClaimsFromToken(authToken)
                    @Suppress("UNCHECKED_CAST")
                    val rolesMap = claims.get("role", List::class.java) as List<String>
                    val auth = UsernamePasswordAuthenticationToken(
                            username, null,
                            rolesMap.map { SimpleGrantedAuthority(it) }.toList()
                    )
                    Mono.just(auth)
                } else {
                    Mono.empty()
                }
            }

    @Bean
    fun securityContextRepository(authenticationManager: ReactiveAuthenticationManager) =
            object : ServerSecurityContextRepository {
                override fun save(exchange: ServerWebExchange?, context: SecurityContext?): Mono<Void> =
                        throw UnsupportedOperationException()

                override fun load(swe: ServerWebExchange): Mono<SecurityContext> {
                    val request = swe.request
                    val authHeader = request.headers.getFirst(HttpHeaders.AUTHORIZATION)

                    return if (authHeader != null && authHeader.startsWith("Bearer ")) {
                        val authToken = authHeader.substring("Bearer ".length)
                        val auth = UsernamePasswordAuthenticationToken(authToken, authToken)
                        authenticationManager
                                .authenticate(auth)
                                .map { SecurityContextImpl(it) }
                    } else {
                        Mono.empty()
                    }
                }

            }
}

