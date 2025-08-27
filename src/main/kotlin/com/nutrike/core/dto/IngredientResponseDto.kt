package com.nutrike.core.dto

import com.nutrike.core.entity.Measure
import java.math.BigDecimal
import java.util.UUID

data class IngredientResponseDto(
    val id: UUID,
    val name: String,
    val measure: Measure,
    val calories: BigDecimal,
    val carbs: BigDecimal,
    val protein: BigDecimal,
    val sugar: BigDecimal,
    val fat: BigDecimal,
)
