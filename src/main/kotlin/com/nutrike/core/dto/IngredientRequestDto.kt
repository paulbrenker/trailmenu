package com.nutrike.core.dto

import com.nutrike.core.entity.Measure
import jakarta.validation.constraints.DecimalMax
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import java.math.BigDecimal

data class IngredientRequestDto(
    @field:NotEmpty(message = "Ingredient name is required")
    @field:Size(min = 2, max = 255, message = "Ingredient name must be between 1 and 255 characters")
    val name: String,
    val measure: Measure,
    @field:NotNull(message = "Calories are required")
    @field:DecimalMax(value = "50.0", message = "Calories must not be greater than 50 per gram")
    val calories: BigDecimal,
    @field:NotNull(message = "Carbs are required")
    @field:DecimalMax(value = "1.0", message = "Carbs must not be greater than 1")
    val carbs: BigDecimal,
    @field:NotNull(message = "Protein is required")
    @field:DecimalMax(value = "1.0", message = "Protein must not be greater than 1")
    val protein: BigDecimal,
    @field:NotNull(message = "Sugar is required")
    @field:DecimalMax(value = "1.0", message = "Sugar must not be greater than 1")
    val sugar: BigDecimal,
    @field:NotNull(message = "Fat is required")
    @field:DecimalMax(value = "1.0", message = "Fat must not be greater than 1")
    val fat: BigDecimal,
)
