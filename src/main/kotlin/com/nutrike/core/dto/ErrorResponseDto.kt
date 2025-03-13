package com.nutrike.core.dto

data class ErrorResponseDto(
    val status: Int,
    val code: String,
    val message: String,
    val target: String? = null,
)
