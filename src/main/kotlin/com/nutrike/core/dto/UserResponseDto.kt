package com.nutrike.core.dto

import com.nutrike.core.entity.RoleEntity

data class UserResponseDto(
    val username: String,
    val approval: Boolean,
    val roles: Set<RoleEntity>,
)
