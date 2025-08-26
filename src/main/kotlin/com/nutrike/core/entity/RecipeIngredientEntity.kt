package com.nutrike.core.entity

import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import jakarta.persistence.EmbeddedId
import jakarta.persistence.Entity
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.MapsId
import jakarta.persistence.Table
import java.io.Serializable
import java.util.UUID

@Entity
@Table(name = "recipe_ingredients")
data class RecipeIngredientEntity(
    @EmbeddedId
    val id: RecipeIngredientId,
    @Column(nullable = false)
    val multiplier: Int,
    @Column(name = "multiplier_description", nullable = false, length = 32)
    val multiplierDescription: String,
    @ManyToOne
    @MapsId("recipeId")
    @JoinColumn(name = "recipe_id", insertable = false, updatable = false)
    val recipe: RecipeEntity,
    @ManyToOne
    @MapsId("ingredientId")
    @JoinColumn(name = "ingredient_id", insertable = false, updatable = false)
    val ingredient: IngredientEntity,
)

@Embeddable
data class RecipeIngredientId(
    @Column(name = "recipe_id")
    val recipeId: UUID,
    @Column(name = "ingredient_id")
    val ingredientId: UUID,
) : Serializable
