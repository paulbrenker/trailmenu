package com.nutrike.core.util

import com.nutrike.core.config.JwtProperties
import com.nutrike.core.exception.InvalidTokenException
import io.mockk.every
import io.mockk.mockkObject
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class JwtUtilTest {
    private val secret = "your-secure-32-byte-secret-key"
    private val jwtProperties = JwtProperties(secret)
    private val jwtUtil = JwtUtil(jwtProperties)

    private val testuser = "test-user"
    private lateinit var token: String
    private lateinit var bearerToken: String

    @BeforeEach
    fun setUp() {
        token = jwtUtil.generateToken(testuser, setOf("USER"))
        bearerToken = "Bearer $token"
    }

    @Test
    fun `should successfully generate token`() {
        val validatedJwt = jwtUtil.validateToken(token)
        assertThat(bearerToken).isEqualTo("Bearer $token")
        assertThat(validatedJwt.subject).isEqualTo(testuser)
        assertThat(validatedJwt["roles"]).isEqualTo(listOf("USER"))
    }

    @Test
    fun `expired token should not be validated`() {
        mockkObject(ClockUtil)
        val fakeTime = System.currentTimeMillis() - 1
        every { ClockUtil.tokenExpirationTime() } returns fakeTime

        val expiredToken = jwtUtil.generateToken(testuser, setOf("USER"))
        val exception = assertThrows<InvalidTokenException> { jwtUtil.validateToken(expiredToken) }

        assertThat(exception.message).contains("Token has expired")
    }

    @Test
    fun `different instances of JwtUtil are not compatible`() {
        val jwtProperties = JwtProperties("another-secure-32-byte-secret-key")
        val jwtUtilNew = JwtUtil(jwtProperties)

        val exception = assertThrows<InvalidTokenException> { jwtUtilNew.validateToken(token) }

        assertThat(exception.message).contains("Token validation failed")
    }
}
