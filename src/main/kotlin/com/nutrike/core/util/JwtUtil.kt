package com.nutrike.core.util

import com.nutrike.core.util.ClockUtil.Companion.tokenExpirationTime
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import org.springframework.stereotype.Component
import java.util.Date

@Component
class JwtUtil {
    private val secret =
        Jwts.SIG.HS256
            .key()
            .build() // TODO persist key in Spring env

    fun generateToken(
        username: String,
        roles: Set<String>,
    ): String =
        Jwts
            .builder()
            .subject(username)
            .claim("roles", roles)
            .issuedAt(Date())
            .expiration(Date(tokenExpirationTime()))
            .signWith(secret)
            .compact()

    fun validateToken(token: String): Claims? =
        try {
            Jwts
                .parser()
                .verifyWith(secret)
                .build()
                .parseSignedClaims(token)
                .payload
        } catch (e: Exception) {
            null
        }
}
