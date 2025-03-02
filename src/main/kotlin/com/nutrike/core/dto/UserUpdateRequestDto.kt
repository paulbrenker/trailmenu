package com.nutrike.core.dto

import com.nutrike.core.entity.RoleEntity
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.Size

data class UserUpdateRequestDto(
    @field:NotEmpty(message = "Username cannot be empty")
    @field:Size(min = 1, max = 150, message = "Username must be between 1 and 150 characters")
    val username: String,
    @field:NotEmpty(message = "Old Password is required")
    @field:Size(min = 1, max = 255, message = "Old Password must be between 1 and 255 characters")
    val oldPassword: String,
    @field:NotEmpty(message = "New Password is required")
    @field:Size(min = 1, max = 255, message = "New Password must be between 1 and 255 characters")
    val newPassword: String,
    val approval: Boolean,
    val roles: List<RoleEntity>,
)
