package io.github.titaniumcoder.toggl.reporting.security

//@Component
//class JwtAuthenticationManager(val jwtService: JwtService) : ReactiveAuthenticationManager {
//    override fun authenticate(authentication: Authentication): Mono<Authentication> {
//        val credentials = authentication.credentials.toString()
//
//        val auth: Authentication? = try {
//            val claims = jwtService.validate(credentials)
//            claims?.let {cl ->
//                UsernamePasswordAuthenticationToken(
//                        cl.subject,
//                        null,
//                        listOf(SimpleGrantedAuthority("USER"))
//                )
//            }
//        } catch (e: Exception) {
//            null
//        }
//
//        return auth?.let { Mono.just(it) } ?: Mono.empty()
//    }
//}
