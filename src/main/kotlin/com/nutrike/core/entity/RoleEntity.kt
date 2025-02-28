package com.nutrike.core.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Id
import jakarta.persistence.Table
import jakarta.validation.constraints.Size

@Entity
@Table(name = "role")
data class RoleEntity(
    @Id
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true)
    @Size(max = 50)
    val type: RoleType,
)

enum class RoleType {
    USER,
    ADMIN,
}
