package com.nutrike.core.dto

import com.nutrike.core.entity.RoleEntity
import java.util.UUID

data class UserResponseDto(
    val id: UUID,
    val username: String,
    val approval: Boolean,
    val roles: Set<RoleEntity>,
)
