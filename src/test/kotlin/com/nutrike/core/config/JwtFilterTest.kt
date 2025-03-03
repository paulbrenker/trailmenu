package com.nutrike.core.config

import com.nutrike.core.config.AuthenticationConfig.Companion.AUTHENTICATION_HEADER
import com.nutrike.core.util.JwtUtil
import io.jsonwebtoken.Claims
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.spyk
import io.mockk.verify
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.User
import java.util.stream.Stream

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class JwtFilterTest {
    private lateinit var jwtFilter: JwtFilter
    private val jwtUtil = mockk<JwtUtil>()

    @BeforeEach
    fun setUp() {
        jwtFilter = spyk(JwtFilter(jwtUtil))
    }

    @Test
    fun `shouldNotFilter should return false on auth`() {
        val mockRequest = mockk<HttpServletRequest>()
        every { mockRequest.requestURI } returns "/user/token"
        every { mockRequest.method } returns "POST"

        assertTrue(
            jwtFilter.shouldNotFilter(mockRequest),
        )
    }

    private fun invalidAuthProvider(): Stream<Arguments> {
        val mockSecurityContext = mockk<SecurityContext>(relaxed = true)

        fun getNotNullContext(mockSecurityContext: SecurityContext): Authentication {
            mockSecurityContext.authentication =
                UsernamePasswordAuthenticationToken(
                    User("test-user", "abc", emptyList()),
                    null,
                    emptyList(),
                )
            return mockSecurityContext.authentication
        }

        return Stream.of(
            Arguments.of(null, false, null, false),
            Arguments.of("starts not with Bearer ", false, null, false),
            Arguments.of("Bearer not.a.token", false, null, false),
            Arguments.of("Bearer not.a.token", true, getNotNullContext(mockSecurityContext), false),
            Arguments.of("Bearer testToken", true, null, true),
        )
    }

    @ParameterizedTest
    @MethodSource("invalidAuthProvider")
    fun `should handle authorization requests correctly`(
        authHeader: String?,
        createClaims: Boolean,
        authentication: Authentication?,
        positiveTestCase: Boolean,
    ) {
        val mockRequest = mockk<HttpServletRequest>(relaxed = true)
        val mockResponse = mockk<HttpServletResponse>(relaxed = true)
        val mockFilterChain = mockk<FilterChain>(relaxed = true)

        mockkStatic(SecurityContextHolder::class)

        var claims: Claims? = null
        if (createClaims) {
            claims =
                mockk<Claims>(relaxed = true, relaxUnitFun = true) {
                    every { subject } returns "test-username"
                    every { get("roles", List::class.java) } returns listOf("USER")
                }
        }

        every { mockRequest.getHeader(AUTHENTICATION_HEADER) } returns authHeader
        every { jwtUtil.validateToken(any()) } returns claims
        every { SecurityContextHolder.getContext().authentication } returns authentication

        if (positiveTestCase) {
            assertThrows<io.mockk.MockKException> {
                // should throw because no SecurityContext exists
                jwtFilter.doFilterInternal(mockRequest, mockResponse, mockFilterChain)
            }
        } else {
            jwtFilter.doFilterInternal(mockRequest, mockResponse, mockFilterChain)
            verify(inverse = true) { SecurityContextHolder.getContext().authentication = any() }
        }
    }
}
