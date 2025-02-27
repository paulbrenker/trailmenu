package com.nutrike.core.util

import com.nutrike.core.config.AuthenticationConfig.Companion.TOKEN_EXPIRATION_TIME

class ClockUtil {
    companion object {
        private fun systemCurrentMillis() = System.currentTimeMillis()

        fun tokenExpirationTime() = systemCurrentMillis() + TOKEN_EXPIRATION_TIME
    }
}
