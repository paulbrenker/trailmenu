package com.nutrike.core.util

import io.mockk.every
import io.mockk.mockkObject
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class JwtUtilTest {
    private val jwtUtil = JwtUtil()

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
        assertThat(validatedJwt!!.subject).isEqualTo(testuser)
        assertThat(validatedJwt["roles"]).isEqualTo(listOf("USER"))
    }

    @Test
    fun `expired token should not be validated`() {
        mockkObject(ClockUtil)
        val fakeTime = System.currentTimeMillis() - 1
        every { ClockUtil.tokenExpirationTime() } returns fakeTime

        val expiredToken = jwtUtil.generateToken(testuser, setOf("USER"))
        val validatedJwt = jwtUtil.validateToken(expiredToken)

        assertThat(validatedJwt).isNull()
    }

    @Test
    fun `different instances of JwtUtil are not compatible`() {
        val jwtUtilNew = JwtUtil()

        val validatedOldToken = jwtUtilNew.validateToken(token)

        assertThat(validatedOldToken).isNull()
    }
}
