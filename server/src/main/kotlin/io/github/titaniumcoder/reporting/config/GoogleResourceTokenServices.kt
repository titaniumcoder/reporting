package io.github.titaniumcoder.reporting.config

import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.jackson2.JacksonFactory
import io.github.titaniumcoder.reporting.user.UserService
import org.slf4j.LoggerFactory
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.oauth2.common.OAuth2AccessToken
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException
import org.springframework.security.oauth2.provider.OAuth2Authentication
import org.springframework.security.oauth2.provider.OAuth2Request
import org.springframework.security.oauth2.provider.token.ResourceServerTokenServices
import org.springframework.stereotype.Service

@Service
class GoogleResourceTokenServices(val config: ReportingConfiguration, val service: UserService) : ResourceServerTokenServices {
    private val log = LoggerFactory.getLogger(GoogleResourceTokenServices::class.java)

    private val verifier: GoogleIdTokenVerifier by lazy {
        val transport = NetHttpTransport()
        val jsonFactory = JacksonFactory()

        GoogleIdTokenVerifier.Builder(transport, jsonFactory)
                .setAudience(listOf(config.clientId))
                .build()
    }

    private fun validateIdToken(idToken: String): String {
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

    override fun loadAuthentication(accessToken: String?): OAuth2Authentication {
        if (accessToken != null) {
            val result = validateIdToken(accessToken)
            val user = service.findByEmail(result)
            if (user != null) {
                val roles = mutableListOf("ROLE_USER")
                if (user.admin)
                    roles.add("ROLE_ADMIN")
                if (user.canViewMoney)
                    roles.add("ROLE_MONEY")
                if (user.canBook)
                    roles.add("ROLE_BOOKING")

                val authorities = roles.map { SimpleGrantedAuthority(it) }
                val storedRequest =
                        OAuth2Request(
                                mapOf(), config.clientId, authorities, true, mutableSetOf(), mutableSetOf(), null, null, null
                        )

                return OAuth2Authentication(
                        storedRequest,
                        UsernamePasswordAuthenticationToken(user.email, "N/A", authorities)
                )
            }
        }
        throw InvalidTokenException(accessToken)
    }

    override fun readAccessToken(accessToken: String?): OAuth2AccessToken {
        TODO("not implemented readAccessToken $accessToken")
    }
}
