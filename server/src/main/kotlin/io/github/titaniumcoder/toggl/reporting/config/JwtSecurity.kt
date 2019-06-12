package io.github.titaniumcoder.toggl.reporting.config

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Component
import java.util.*
import javax.crypto.spec.SecretKeySpec

data class AuthRequest(val username: String, val password: String)

data class AuthResponse(val token: String)

@Component
class JWTUtil(private val configuration: TogglConfiguration) {
    fun getAllClaimsFromToken(token: String): Claims =
            Jwts
                    .parser()
                    .setSigningKey(Base64.getEncoder().encodeToString(configuration.security.secret.toByteArray()))
                    .parseClaimsJws(token)
                    .body

    fun getUsernameFromToken(token: String): String {
        return getAllClaimsFromToken(token).subject
    }

    fun getExpirationDateFromToken(token: String): Date {
        return getAllClaimsFromToken(token).expiration
    }

    private fun isTokenExpired(token: String): Boolean {
        val expiration = getExpirationDateFromToken(token)
        return expiration.before(Date())
    }

    fun generateToken(user: UserDetails): String {
        val claims = HashMap<String, Any>()
        claims["role"] = user.authorities.map { it.authority }
        return doGenerateToken(claims, user.username)
    }

    private fun doGenerateToken(claims: Map<String, Any>, username: String): String {
        val expirationTimeLong = configuration.security.expiration

        val createdDate = Date()
        val expirationDate = Date(createdDate.time + expirationTimeLong * 1000)

        val secretKey = SecretKeySpec(
                configuration.security.secret.toByteArray(),
                SignatureAlgorithm.HS256.jcaName
        )
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(createdDate)
                .setExpiration(expirationDate)
                .signWith(secretKey)
                .compact()
    }

    fun validateToken(token: String): Boolean {
        return (!isTokenExpired(token))
    }
}