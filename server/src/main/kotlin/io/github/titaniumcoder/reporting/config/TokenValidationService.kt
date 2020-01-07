package io.github.titaniumcoder.reporting.config

import reactor.core.publisher.Mono

interface TokenValidationService {
    fun validateIdToken(idToken: String): Mono<String>
}
