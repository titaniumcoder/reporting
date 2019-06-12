package io.github.titaniumcoder.toggl.reporting.config

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.userdetails.ReactiveUserDetailsService
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono


@RestController
class LoginController(
        private val jwtUtil: JWTUtil,
        private val passwordEncoder: PasswordEncoder,
        private val userService: ReactiveUserDetailsService
) {
    @RequestMapping("/api/login", method = [RequestMethod.POST])
    fun login(@RequestBody ar: AuthRequest): Mono<ResponseEntity<AuthResponse>> =
            userService.findByUsername(ar.username).map { userDetails ->
                if (passwordEncoder.matches(ar.password, userDetails.password)) {
                    ResponseEntity.ok(AuthResponse(jwtUtil.generateToken(userDetails)))
                } else {
                    ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
                }
            }.defaultIfEmpty(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build())
}