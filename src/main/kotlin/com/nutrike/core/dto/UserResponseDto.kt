package com.nutrike.core.dto

import com.nutrike.core.entity.RoleEntity
import java.sql.Timestamp

data class UserResponseDto(
    val username: String,
    val addedDate: Timestamp,
    val roles: Set<RoleEntity>,
)
