package com.nutrike.core.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.math.BigDecimal
import java.util.UUID

@Entity
@Table(name = "ingredient")
data class IngredientEntity(
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID? = null,
    @Column(nullable = false, unique = true, length = 255, name = "name")
    val name: String,
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 2, name = "measure")
    val measure: Measure,
    @Column(nullable = false, name = "calories")
    val calories: BigDecimal,
    @Column(nullable = false, name = "carbs")
    val carbs: BigDecimal,
    @Column(nullable = false, name = "protein")
    val protein: BigDecimal,
    @Column(nullable = false, name = "sugar")
    val sugar: BigDecimal,
    @Column(nullable = false, name = "fat")
    val fat: BigDecimal,
)
