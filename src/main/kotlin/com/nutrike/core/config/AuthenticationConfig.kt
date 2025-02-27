package com.nutrike.core.config

class AuthenticationConfig {
    companion object {
        // Paths that will be excluded from authentication
        val AUTHENTICATION_EXCLUDE = listOf("/actuator/**", "/auth", "/swagger-ui/**", "/v3/api-docs/**")
        const val TOKEN_PREFIX = "Bearer "
        const val AUTHENTICATION_HEADER = "Authorization"
        const val TOKEN_EXPIRATION_TIME = 1000 * 60 * 60
    }
}
