package io.github.titaniumcoder.reporting.config

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import javax.inject.Singleton

//@Singleton
class JwtContextRepository(
//        val authenticationManager: ReactiveAuthenticationManager
)/*  : ServerSecurityContextRepository  */{
//    override fun save(exchange: ServerWebExchange?, context: SecurityContext?): Mono<Void> =
//            Mono.error(UnsupportedOperationException("saving is not supported here"))
//
//    override fun load(swe: ServerWebExchange): Mono<SecurityContext> {
//        val request = swe.request
//        val authHeader = request.headers.getFirst(HttpHeaders.AUTHORIZATION)
//        val authToken = if (authHeader != null && authHeader.startsWith(tokenPrefix)) {
//            authHeader.substring(prefixLength)
//        } else {
//            log.debug("Couldn't find the bearer token, will ignore the header.")
//            null
//        }
//        return if (authToken != null) {
//            val auth = UsernamePasswordAuthenticationToken(authToken, authToken)
//            this.authenticationManager.authenticate(auth).map { SecurityContextImpl(it) }
//        } else {
//            Mono.empty()
//        }
//    }

    companion object {
        private val log: Logger = LoggerFactory.getLogger(JwtContextRepository::class.java)
        private const val tokenPrefix = "Bearer "
        private const val prefixLength = tokenPrefix.length
    }
}
