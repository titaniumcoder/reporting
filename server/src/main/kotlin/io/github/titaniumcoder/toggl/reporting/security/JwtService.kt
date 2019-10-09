package io.github.titaniumcoder.toggl.reporting.security

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import org.springframework.stereotype.Component
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.*
import javax.crypto.SecretKey

@Component
class JwtService {
    val secretKey: SecretKey by lazy { Keys.secretKeyFor(SignatureAlgorithm.HS256) }

    fun createJws(subject: String): String =
        Jwts.builder()
                .setId(UUID.randomUUID().toString())
                .setSubject(subject)
                .signWith(secretKey)
                .setExpiration(Date.from(Instant.now().plus(1, ChronoUnit.HOURS)))
                .compact()

    fun validate(jws: String): Claims? {
        val claims = Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(jws)
                .body

        return if (claims.expiration.before(Date())) null else claims
    }
}

data class AuthRequest(val username: String, val password: String)

data class AuthResponse(val token: String)

