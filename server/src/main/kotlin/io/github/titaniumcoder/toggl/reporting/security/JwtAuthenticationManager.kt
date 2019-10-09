package io.github.titaniumcoder.toggl.reporting.security

import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class JwtAuthenticationManager(val jwtService: JwtService) : ReactiveAuthenticationManager {
    override fun authenticate(authentication: Authentication): Mono<Authentication> {
        val credentials = authentication.credentials.toString()

        val auth: Authentication? = try {
            val claims = jwtService.validate(credentials)
            claims?.let {cl ->
                UsernamePasswordAuthenticationToken(
                        cl.subject,
                        null,
                        listOf(SimpleGrantedAuthority("USER"))
                )
            }
        } catch (e: Exception) {
            null
        }

        return auth?.let { Mono.just(it) } ?: Mono.empty()
    }
}
