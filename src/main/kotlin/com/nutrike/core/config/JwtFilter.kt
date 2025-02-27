package com.nutrike.core.config

import com.nutrike.core.config.AuthentificationConfig.Companion.AUTHENTICATION_EXCLUDE
import com.nutrike.core.config.AuthentificationConfig.Companion.AUTHENTICATION_HEADER
import com.nutrike.core.config.AuthentificationConfig.Companion.TOKEN_PREFIX
import com.nutrike.core.util.JwtUtil
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.User
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class JwtFilter(
    private val jwtUtil: JwtUtil,
) : OncePerRequestFilter() {
    public override fun shouldNotFilter(request: HttpServletRequest): Boolean =
        AUTHENTICATION_EXCLUDE
            .contains(request.requestURI)

    public override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        chain: FilterChain,
    ) {
        val authHeader = request.getHeader(AUTHENTICATION_HEADER)

        if (authHeader != null && authHeader.startsWith(TOKEN_PREFIX)) {
            val token = authHeader.substring(7)
            val username = jwtUtil.validateToken(token)

            if (username != null && SecurityContextHolder.getContext().authentication == null) {
                val userDetails = User(username, "", emptyList())
                val authentication =
                    UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.authorities,
                    )
                SecurityContextHolder.getContext().authentication = authentication
            }
        }

        chain.doFilter(request, response)
    }
}
