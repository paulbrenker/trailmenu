package com.nutrike.core.config

class AuthentificationConfig {
    companion object {
        // Paths that will be excluded from authentication
        val AUTHENTICATION_EXCLUDE = listOf("/auth")
        const val TOKEN_PREFIX = "Bearer "
        const val AUTHENTICATION_HEADER = "Authorization"
        const val TOKEN_EXPIRATION_TIME = 1000 * 60 * 60
    }
}
