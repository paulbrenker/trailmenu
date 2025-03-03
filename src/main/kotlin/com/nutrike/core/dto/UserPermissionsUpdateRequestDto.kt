package com.nutrike.core.dto

import com.nutrike.core.entity.RoleEntity

data class UserPermissionsUpdateRequestDto(
    val approval: Boolean,
    val roles: List<RoleEntity>,
)
