package io.github.titaniumcoder.toggl.reporting.security

import javax.crypto.SecretKey

//@Component
class JwtService {
    val secretKey: SecretKey by lazy { TODO() /*  Keys.secretKeyFor(SignatureAlgorithm.HS256) */ }

    fun createJws(subject: String): String = TODO()
//        Jwts.builder()
//                .setId(UUID.randomUUID().toString())
//                .setSubject(subject)
//                .signWith(secretKey)
//                .setExpiration(Date.from(Instant.now().plus(1, ChronoUnit.HOURS)))
//                .compact()

    fun validate(jws: String): String? = TODO()
//    Claims? {
//        val claims = Jwts.parser()
//                .setSigningKey(secretKey)
//                .parseClaimsJws(jws)
//                .body
//
//        return if (claims.expiration.before(Date())) null else claims
//    }
}

data class AuthRequest(val username: String, val password: String)

data class AuthResponse(val token: String)

