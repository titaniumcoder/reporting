package io.github.titaniumcoder.reporting.config

import javax.inject.Singleton

//@Singleton
class JwtAuthenticationManager(
        val tokenProvider: TokenValidationService
//        val userDetailsService: ReactiveUserDetailsService
) /* : ReactiveAuthenticationManager  */ {
//    override fun authenticate(authentication: Authentication): Mono<Authentication> =
//            tokenProvider
//                    .validateIdToken(authentication.credentials.toString())
//                    .flatMap { userDetailsService.findByUsername(it) }
//                    .map {
//                        UsernamePasswordAuthenticationToken(it.username, "N/A", it.authorities) as Authentication
//                    }
//                    .log()
}
