package com.nutrike.core.exception

data class InvalidTokenException(
    override val message: String,
) : RuntimeException(message)
