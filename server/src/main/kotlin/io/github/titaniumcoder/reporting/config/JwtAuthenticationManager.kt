package io.github.titaniumcoder.reporting.config

import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.userdetails.ReactiveUserDetailsService
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class JwtAuthenticationManager(val tokenProvider: TokenValidationService, val userDetailsService: ReactiveUserDetailsService) : ReactiveAuthenticationManager {
    override fun authenticate(authentication: Authentication): Mono<Authentication> =
            tokenProvider
                    .validateIdToken(authentication.credentials.toString())
                    .flatMap { userDetailsService.findByUsername(it) }
                    .map {
                        UsernamePasswordAuthenticationToken(it.username, "N/A", it.authorities) as Authentication
                    }
                    .log()
}
