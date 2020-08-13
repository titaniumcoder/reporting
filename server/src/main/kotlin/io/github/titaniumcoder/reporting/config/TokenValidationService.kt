package io.github.titaniumcoder.reporting.config

interface TokenValidationService {
    fun validateIdToken(idToken: String): String?
}
