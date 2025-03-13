package com.nutrike.core.util

import com.nutrike.core.config.JwtProperties
import com.nutrike.core.exception.InvalidTokenException
import com.nutrike.core.util.ClockUtil.Companion.tokenExpirationTime
import io.jsonwebtoken.Claims
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.MalformedJwtException
import io.jsonwebtoken.security.Keys
import org.springframework.stereotype.Component
import java.security.MessageDigest
import java.util.Date

@Component
class JwtUtil(
    jwtProperties: JwtProperties,
) {
    private val key =
        Keys.hmacShaKeyFor(
            MessageDigest
                .getInstance("SHA-256")
                .digest(jwtProperties.secret.toByteArray()),
        )

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
            .signWith(key)
            .compact()

    fun validateToken(token: String): Claims =
        try {
            Jwts
                .parser()
                .verifyWith(key)
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
