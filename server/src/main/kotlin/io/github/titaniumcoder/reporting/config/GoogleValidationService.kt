package io.github.titaniumcoder.reporting.config

import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.jackson2.JacksonFactory
import org.slf4j.LoggerFactory
import org.springframework.cache.annotation.Cacheable
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException
import org.springframework.stereotype.Service

@Service
class GoogleValidationService(val config: ReportingConfiguration) {
    private val log = LoggerFactory.getLogger(GoogleResourceTokenServices::class.java)

    private val verifier: GoogleIdTokenVerifier by lazy {
        val transport = NetHttpTransport()
        val jsonFactory = JacksonFactory()

        GoogleIdTokenVerifier.Builder(transport, jsonFactory)
                .setAudience(listOf(config.clientId))
                .build()
    }

    @Cacheable("tokens")
    fun validateIdToken(idToken: String): String {
        val result = verifier.verify(idToken)

        if (result != null) {
            val payload = result.payload

            val email = payload.email
            val verified = payload.emailVerified

            if (verified) {
                log.debug("Loading user with email $email")
                return email
            } else {
                throw InvalidTokenException(idToken)
            }
        } else {
            throw InvalidTokenException(idToken)
        }
    }
}
