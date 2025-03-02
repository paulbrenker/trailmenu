package com.nutrike.core.config

import org.springframework.http.HttpMethod

class AuthenticationConfig {
    companion object {
        // Paths that will be excluded from authentication
        val AUTHENTICATION_EXCLUDE: Map<String, HttpMethod> =
            mapOf(
                "/actuator/**" to HttpMethod.GET,
                "/user/token" to HttpMethod.POST,
                "/user" to HttpMethod.POST,
                "swagger-ui/**" to HttpMethod.GET,
                "/v3/api-docs/**" to HttpMethod.GET,
            )
        val ADMIN_RIGHTS_REQUIRED =
            mapOf(
                "/user/{id}/approval" to HttpMethod.PUT,
                "/user" to HttpMethod.GET,
            )
        const val TOKEN_PREFIX = "Bearer "
        const val AUTHENTICATION_HEADER = "Authorization"
        const val TOKEN_EXPIRATION_TIME = 1000 * 60 * 60
    }
}
