package com.nutrike.core.entity

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

class RecipeEntityTest {
    @Test
    fun `new Recipe date time is set correctly by default`() {
        val recipeEntity =
            RecipeEntity(
                name = "fishsticks",
                type = RecipeType.DINNER,
                instructions =
                    """This is how to make 
				|fishsticks
                    """.trimMargin(),
                creator = "johndoe",
                recipeIngredients = mutableSetOf<RecipeIngredientEntity>(),
            )

        assertThat(recipeEntity.addedDate.toLocalDateTime())
            .isAfter(LocalDateTime.now().minusSeconds(1))
    }
}
