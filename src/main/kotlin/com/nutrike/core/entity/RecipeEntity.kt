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
import java.util.UUID

@Entity
@Table(name = "recipe")
data class RecipeEntity(
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID?,
    @Column(nullable = false, length = 255, name = "name")
    val name: String,
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16, name = "type")
    val type: RecipeType,
    @Column(nullable = false, name = "instructions")
    val instructions: String,
    @OneToMany(mappedBy = "recipe", cascade = [CascadeType.ALL], orphanRemoval = true)
    val recipeIngredients: MutableSet<RecipeIngredientEntity>,
)
