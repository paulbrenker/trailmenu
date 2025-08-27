package com.nutrike.core.entity

import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import java.sql.Timestamp
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import java.util.UUID

@Entity
@Table(name = "recipe")
data class RecipeEntity(
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID? = null,
    @Column(nullable = false, unique = true, length = 255, name = "name")
    val name: String,
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16, name = "type")
    val type: RecipeType,
    @Column(nullable = false, name = "instructions")
    val instructions: String,
    @Column(name = "added_date", nullable = false, updatable = false)
    val addedDate: Timestamp =
        Timestamp.valueOf(
            LocalDateTime.now().truncatedTo(ChronoUnit.MICROS),
        ),
    @Column(name = "creator", length = 150, nullable = false)
    val creator: String,
    @OneToMany(mappedBy = "recipe", cascade = [CascadeType.ALL], orphanRemoval = true)
    val recipeIngredients: MutableSet<RecipeIngredientEntity>,
)
