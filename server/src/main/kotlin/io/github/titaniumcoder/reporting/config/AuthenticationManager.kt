package io.github.titaniumcoder.reporting.config

import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.ReactiveUserDetailsService
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class AuthenticationManager(val tokenProvider: TokenValidationService, val userDetailsService: ReactiveUserDetailsService) : ReactiveAuthenticationManager {
    override fun authenticate(authentication: Authentication): Mono<Authentication>? {
        val authToken = authentication.credentials.toString()
        return tokenProvider
                .validateIdToken(authToken)
                .flatMap { userDetailsService.findByUsername(it) }
                .map {
                    val a = UsernamePasswordAuthenticationToken(it.username, "N/A", it.authorities)
                    SecurityContextHolder.getContext().authentication = a // FIXME remove this
                    a
                }
    }
}
