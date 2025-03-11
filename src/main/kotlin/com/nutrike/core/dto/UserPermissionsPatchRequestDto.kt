package com.nutrike.core.dto

import com.nutrike.core.entity.RoleEntity

data class UserPermissionsPatchRequestDto(
    val roles: List<RoleEntity>,
)
