package com.nutrike.core.entity

import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.JoinTable
import jakarta.persistence.ManyToMany
import jakarta.persistence.Table
import java.util.UUID

@Entity
@Table(name = "users")
data class UserEntity(
    @Id
    @GeneratedValue
    val id: UUID? = null,
    @Column(nullable = false, unique = true, length = 150, name = "username")
    val username: String,
    @Column(nullable = false, length = 255, name = "password")
    val password: String,
    @Column(nullable = false, name = "approval")
    val approval: Boolean = false,
    @ManyToMany(fetch = FetchType.EAGER, cascade = [CascadeType.ALL])
    @JoinTable(
        name = "users_roles",
        joinColumns = [JoinColumn(name = "user_id")],
        inverseJoinColumns = [JoinColumn(name = "role")],
    )
    val roles: Set<RoleEntity> = setOf(),
)
