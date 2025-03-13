package com.nutrike.core.config

import com.nutrike.core.config.AuthenticationConfig.Companion.AUTHENTICATION_EXCLUDE
import com.nutrike.core.config.AuthenticationConfig.Companion.AUTHENTICATION_HEADER
import com.nutrike.core.config.AuthenticationConfig.Companion.TOKEN_PREFIX
import com.nutrike.core.config.handler.CustomUnauthorizedHandler.handle
import com.nutrike.core.exception.InvalidTokenException
import com.nutrike.core.util.JwtUtil
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class JwtFilter(
    private val jwtUtil: JwtUtil,
) : OncePerRequestFilter() {
    public override fun shouldNotFilter(request: HttpServletRequest): Boolean =
        AUTHENTICATION_EXCLUDE
            .filter { (path, method) ->
                (path == request.requestURI) && (method.toString() == request.method)
            }.isNotEmpty()

    public override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        chain: FilterChain,
    ) {
        try {
            val authHeader = request.getHeader(AUTHENTICATION_HEADER)

            if (authHeader != null && authHeader.startsWith(TOKEN_PREFIX)) {
                val token = authHeader.substring(TOKEN_PREFIX.length)
                val claims = jwtUtil.validateToken(token)

                if (SecurityContextHolder.getContext().authentication == null) {
                    val username = claims.subject
                    val roles = claims["roles"] as? List<String> ?: emptyList()
                    val authorities = roles.map { SimpleGrantedAuthority("ROLE_$it") }

                    val authentication =
                        UsernamePasswordAuthenticationToken(username, null, authorities)
                    SecurityContextHolder.getContext().authentication = authentication
                }
            }
            chain.doFilter(request, response)
        } catch (e: InvalidTokenException) {
            handle(request, response, e)
        }
    }
}
