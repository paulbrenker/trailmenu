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
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
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

        assertTrue(
            jwtFilter.shouldNotFilter(mockRequest),
        )
    }

    private fun invalidAuthProvider(): Stream<Arguments> {
        val mockSecurityContext = mockk<SecurityContext>(relaxed = true)

        fun getNotNullContext(mockSecurityContext: SecurityContext): SecurityContext {
            mockSecurityContext.authentication =
                UsernamePasswordAuthenticationToken(
                    User("test-user", "abc", emptyList()),
                    null,
                    emptyList(),
                )
            return mockSecurityContext
        }
        return Stream.of(
            Arguments.of(null, null, mockSecurityContext),
            Arguments.of("starts not with Bearer ", null, mockSecurityContext),
            Arguments.of("Bearer not.a.token", null, mockSecurityContext),
            Arguments.of(
                "Bearer not.a.token",
                "test-user",
                getNotNullContext(mockSecurityContext),
            ),
        )
    }

    @ParameterizedTest
    @MethodSource("invalidAuthProvider")
    fun `should not authorize invalid requests`(
        authHeader: String?,
        username: String?,
        securityContext: SecurityContext?,
    ) {
        val mockRequest = mockk<HttpServletRequest>(relaxed = true)
        val mockResponse = mockk<HttpServletResponse>(relaxed = true)
        val mockFilterChain = mockk<FilterChain>(relaxed = true)
        val mockClaims =
            mockk<Claims> {
                every { subject } returns username
                every { get("roles", List::class.java) } returns listOf("USER")
            }

        mockkStatic(SecurityContextHolder::class)

        every { mockRequest.getHeader(AUTHENTICATION_HEADER) } returns authHeader
        every { jwtUtil.validateToken(any()) } returns mockClaims
        every { SecurityContextHolder.getContext() } returns securityContext

        jwtFilter.doFilterInternal(mockRequest, mockResponse, mockFilterChain)

        verify(inverse = true) { SecurityContextHolder.getContext().authentication = any() }
    }
}
