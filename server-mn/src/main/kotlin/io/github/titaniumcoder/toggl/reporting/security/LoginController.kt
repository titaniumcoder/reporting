package io.github.titaniumcoder.toggl.reporting.security

import io.github.titaniumcoder.toggl.reporting.config.TogglConfiguration
import io.micronaut.http.HttpStatus

//@RestController
//@RequestMapping("/api")
class AuthenticationREST(val jwtService: JwtService, val config: TogglConfiguration) {
    //    @PostMapping("/login")
    fun login(ar: AuthRequest):  /* Mono<ResponseEntity<*>> */ HttpStatus = TODO()
//{
//        val response =
//                if (ar.username == config.security.username && ar.password == config.security.password) {   // TODO config!
//                    ResponseEntity.ok(AuthResponse(jwtService.createJws(ar.username)))
//                } else {
//                    ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
//                }
//
//        return Mono.just(response)
//    }

}
