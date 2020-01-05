package io.github.titaniumcoder.reporting.config

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpHeaders
import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.context.SecurityContextImpl
import org.springframework.security.core.userdetails.ReactiveUserDetailsService
import org.springframework.security.web.server.context.ServerSecurityContextRepository
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono


@Component
class JwtContextRepository(val authenticationManager: ReactiveAuthenticationManager) : ServerSecurityContextRepository {
    override fun save(exchange: ServerWebExchange?, context: SecurityContext?): Mono<Void> =
            Mono.error(UnsupportedOperationException("saving is not supported here"))

    override fun load(swe: ServerWebExchange): Mono<SecurityContext> {
        val request = swe.request
        val authHeader = request.headers.getFirst(HttpHeaders.AUTHORIZATION)
        val authToken = if (authHeader != null && authHeader.startsWith(tokenPrefix)) {
            authHeader.substring(prefixLength)
        } else {
            log.debug("Couldn't find the bearer token, will ignore the header.")
            null
        }
        return if (authToken != null) {
            val auth = UsernamePasswordAuthenticationToken(authToken, authToken)
            this.authenticationManager.authenticate(auth).map { SecurityContextImpl(it) }
        } else {
            Mono.empty()
        }
    }

    companion object {
        private val log: Logger = LoggerFactory.getLogger(JwtContextRepository::class.java)
        private const val tokenPrefix = "Bearer "
        private const val prefixLength = tokenPrefix.length
    }
}

@Component
class AuthenticationManager(val tokenProvider: TokenValidationService, val userDetailsService: ReactiveUserDetailsService) : ReactiveAuthenticationManager {
    override fun authenticate(authentication: Authentication): Mono<Authentication>? {
        val authToken = authentication.credentials.toString()
        return tokenProvider
                .validateIdToken(authToken)
                .flatMap { userDetailsService.findByUsername(it) }
                .map {
                    val a = UsernamePasswordAuthenticationToken(it.username, "N/A", it.authorities)
                    SecurityContextHolder.getContext().authentication = a // TODO is this really right?
                    a
                }
    }
}
