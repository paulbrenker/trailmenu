package com.nutrike.core.util

import com.nutrike.core.exception.InvalidTokenException
import com.nutrike.core.util.ClockUtil.Companion.tokenExpirationTime
import io.jsonwebtoken.Claims
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.MalformedJwtException
import org.springframework.stereotype.Component
import java.util.Date

@Component
class JwtUtil {
    private val secret =
        Jwts.SIG.HS256
            .key()
            .build()

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

    fun validateToken(token: String): Claims =
        try {
            Jwts
                .parser()
                .verifyWith(secret)
                .build()
                .parseSignedClaims(token)
                .payload
        } catch (e: ExpiredJwtException) {
            throw InvalidTokenException("Token has expired")
        } catch (e: MalformedJwtException) {
            throw InvalidTokenException("Invalid token format")
        } catch (e: Exception) {
            throw InvalidTokenException("Token validation failed")
        }
}
