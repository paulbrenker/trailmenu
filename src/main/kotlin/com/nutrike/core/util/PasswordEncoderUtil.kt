package com.nutrike.core.util

import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component

@Component
class PasswordEncoderUtil(
    private val passwordEncoder: PasswordEncoder,
) {
    fun encodePassword(rawPassword: String): String = passwordEncoder.encode(rawPassword)

    fun verifyPassword(
        rawPassword: String,
        encodedPassword: String,
    ): Boolean = passwordEncoder.matches(rawPassword, encodedPassword)
}
