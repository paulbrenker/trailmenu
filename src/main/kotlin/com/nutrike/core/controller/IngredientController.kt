package com.nutrike.core.controller

import com.nutrike.core.dto.IngredientRequestDto
import com.nutrike.core.dto.IngredientResponseDto
import com.nutrike.core.dto.PageDto
import com.nutrike.core.service.IngredientService
import com.nutrike.core.util.ValidCursor
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.Size
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@Tag(name = "Ingredients", description = "Managing creation and search for ingredients")
@RequestMapping("/ingredient")
@Validated
class IngredientController {
    @Autowired
    internal lateinit var service: IngredientService

    @Operation(
        summary = "Find ingredients",
        description = "Returns a paginated list of ingredients. Can filter for Ingredient name substring search",
        responses = [
            ApiResponse(
                responseCode = "200",
                content = [
                    Content(mediaType = MediaType.APPLICATION_JSON_VALUE),
                ],
            ),
        ],
    )
    @GetMapping
    fun getIngredients(
        @RequestParam @Size(max = 20) q: String? = null,
        @RequestParam @Min(1) @Max(100) limit: Int = 20,
        @ValidCursor @RequestParam cursor: String?,
    ): ResponseEntity<PageDto<IngredientResponseDto>> = service.findIngredientsPaginated(q, limit, cursor)

    @Operation(
        summary = "Add ingredient",
        description = "Adds a new ingredient, returns the created ingredient",
        responses = [
            ApiResponse(
                responseCode = "200",
                content = [
                    Content(mediaType = MediaType.APPLICATION_JSON_VALUE),
                ],
            ),
        ],
    )
    @PostMapping
    fun addIngredient(
        @Valid @RequestBody ingredientInsert: IngredientRequestDto,
    ): ResponseEntity<IngredientResponseDto> = service.insertIngredient(ingredientInsert)
}
