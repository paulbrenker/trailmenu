package com.nutrike.core.entity

import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.JoinTable
import jakarta.persistence.ManyToMany
import jakarta.persistence.Table
import java.sql.Timestamp
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

@Entity
@Table(name = "users")
data class UserEntity(
    @Id
    @Column(nullable = false, unique = true, length = 150, name = "username")
    val username: String,
    @Column(nullable = false, length = 255, name = "password")
    val password: String,
    @Column(name = "added_date", nullable = false, updatable = false)
    val addedDate: Timestamp =
        Timestamp.valueOf(
            LocalDateTime.now().truncatedTo(ChronoUnit.MICROS),
        ),
    @ManyToMany(fetch = FetchType.EAGER, cascade = [CascadeType.MERGE])
    @JoinTable(
        name = "users_roles",
        joinColumns = [JoinColumn(name = "user_id")],
        inverseJoinColumns = [JoinColumn(name = "role")],
    )
    val roles: Set<RoleEntity> = setOf(RoleEntity()),
)
