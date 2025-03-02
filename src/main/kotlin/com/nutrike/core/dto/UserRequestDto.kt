package com.nutrike.core.dto

import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.Size

data class UserRequestDto(
    @field:NotEmpty(message = "Username is required")
    @field:Size(min = 1, max = 150, message = "Username must be between 1 and 150 characters")
    val username: String,
    @field:NotEmpty(message = "Password is required")
    @field:Size(min = 1, max = 255)
    val password: String,
)
