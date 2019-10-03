package io.github.titaniumcoder.toggl.reporting.security

import io.github.titaniumcoder.toggl.reporting.config.TogglConfiguration
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/api")
class AuthenticationREST(val jwtService: JwtService, val config: TogglConfiguration) {
    @PostMapping("/login")
    fun login(@RequestBody ar: AuthRequest): Mono<ResponseEntity<*>> {
        val response =
                if (ar.username == config.security.username && ar.password == config.security.password) {   // TODO config!
                    ResponseEntity.ok(AuthResponse(jwtService.createJws(ar.username)))
                } else {
                    ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
                }

        return Mono.just(response)
    }

}
