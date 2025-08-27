package com.nutrike.core.service

import com.nutrike.core.entity.IngredientEntity
import com.nutrike.core.entity.Measure
import com.nutrike.core.repo.IngredientRepository
import io.mockk.every
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import java.util.UUID
import java.util.stream.Stream

@ExtendWith(MockKExtension::class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class IngredientServiceTest {
    private val ingredientRepository: IngredientRepository = mockk()
    private val service = IngredientService()

    init {
        service.ingredientRepository = ingredientRepository
    }

    private fun paginatedIngredientListProvider() =
        Stream.of(
            Arguments.of(
                null,
                2,
                null,
                PageImpl(
                    listOf(
                        IngredientEntity(
                            id = UUID.randomUUID(),
                            name = "Cheese",
                            measure = Measure.GR,
                            calories = 3.0.toBigDecimal(),
                            carbs = 0.1.toBigDecimal(),
                            protein = 0.1.toBigDecimal(),
                            sugar = 0.1.toBigDecimal(),
                            fat = 0.1.toBigDecimal(),
                        ),
                    ),
                    PageRequest.of(0, 1),
                    1L,
                ),
                1L,
                false,
            ),
        )

    @ParameterizedTest
    @MethodSource("paginatedIngredientListProvider")
    fun `filter and pagination works as expected`(
        q: String?,
        limit: Int,
        cursor: String?,
        response: Page<IngredientEntity>,
        totalCount: Long,
        hasNextPage: Boolean,
    ) {
        every { ingredientRepository.findAll(any(), Pageable.ofSize(limit)) } returns response
        every { ingredientRepository.count() } returns totalCount

        val result = service.findIngredientsPaginated(q, limit, cursor)

        assertThat(result.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(result.body!!.totalCount).isEqualTo(totalCount)
        assertThat(result.body!!.pageInfo.pageSize).isEqualTo(response.content.size)
        assertThat { result.body!!.pageInfo.hasNext == hasNextPage }
    }
}
