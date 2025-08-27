package com.nutrike.core.service

import com.nutrike.core.dto.IngredientRequestDto
import com.nutrike.core.dto.IngredientResponseDto
import com.nutrike.core.dto.PageDto
import com.nutrike.core.dto.PageInfoDto
import com.nutrike.core.entity.IngredientEntity
import com.nutrike.core.repo.IngredientRepository
import com.nutrike.core.repo.IngredientSpecification
import com.nutrike.core.util.CursorUtil
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Pageable
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service

@Service
class IngredientService {
    @Autowired
    internal lateinit var ingredientRepository: IngredientRepository

    fun findIngredientsPaginated(
        q: String?,
        limit: Int,
        cursor: String?,
    ): ResponseEntity<PageDto<IngredientResponseDto>> =
        ingredientRepository
            .findAll(
                IngredientSpecification(q, cursor),
                Pageable.ofSize(limit),
            ).let {
                ResponseEntity.ok(
                    PageDto<IngredientResponseDto>(
                        pageInfo =
                            PageInfoDto(
                                pageSize = it.content.size,
                                hasNext = it.hasNext(),
                                endCursor =
                                    it.content.lastOrNull()?.name?.let { name ->
                                        CursorUtil.getEncodedCursor(mapOf("name" to name), it.hasNext())
                                    },
                            ),
                        totalCount = ingredientRepository.count().toInt(),
                        data = it.content.map(::ingredientEntityToResponseDto),
                    ),
                )
            }

    fun insertIngredient(ingredientRequest: IngredientRequestDto): ResponseEntity<IngredientResponseDto> =
        ResponseEntity.ok(
            ingredientEntityToResponseDto(
                ingredientRepository.save(
                    IngredientEntity(
                        name = ingredientRequest.name,
                        measure = ingredientRequest.measure,
                        calories = ingredientRequest.calories,
                        carbs = ingredientRequest.carbs,
                        protein = ingredientRequest.protein,
                        sugar = ingredientRequest.sugar,
                        fat = ingredientRequest.fat,
                    ),
                ),
            ),
        )

    private fun ingredientEntityToResponseDto(ingredientEntity: IngredientEntity) =
        IngredientResponseDto(
            id = ingredientEntity.id!!,
            name = ingredientEntity.name,
            measure = ingredientEntity.measure,
            calories = ingredientEntity.calories,
            carbs = ingredientEntity.carbs,
            protein = ingredientEntity.protein,
            sugar = ingredientEntity.sugar,
            fat = ingredientEntity.fat,
        )
}
