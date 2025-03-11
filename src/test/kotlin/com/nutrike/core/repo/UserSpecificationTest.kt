package com.nutrike.core.repo

import com.nutrike.core.entity.RoleEntity
import com.nutrike.core.entity.RoleType
import com.nutrike.core.entity.UserEntity
import com.nutrike.core.util.BaseContainerTest
import com.nutrike.core.util.CursorUtil
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Pageable

class UserSpecificationTest : BaseContainerTest() {
    @Autowired
    private lateinit var repository: UserRepository

    @AfterEach
    fun clean() {
        repository.deleteAll()
    }

    @Test
    fun `paginate users successfully`() {
        repository.saveAll((1..25).map { UserEntity("user$it", "password") })

        val firstPageResult = repository.findAll(UserSpecification(null, null), Pageable.ofSize(10))

        assertThat(firstPageResult.hasNext()).isTrue
        assertThat(firstPageResult.totalElements).isEqualTo(25)
        assertThat(firstPageResult.content.size).isEqualTo(10)

        val firstPageCursor =
            CursorUtil.getEncodedCursor(
                mapOf(
                    "username" to
                        firstPageResult.content
                            .last()
                            .username,
                ),
                firstPageResult.hasNext(),
            )

        val secondPageResult = repository.findAll(UserSpecification(firstPageCursor, null), Pageable.ofSize(10))
        assertThat(secondPageResult.hasNext()).isTrue
        assertThat(secondPageResult.totalElements).isEqualTo(15)
        assertThat(secondPageResult.content.size).isEqualTo(10)

        val secondPageCursor =
            CursorUtil.getEncodedCursor(
                mapOf(
                    "username" to
                        secondPageResult.content
                            .last()
                            .username,
                ),
                secondPageResult.hasNext(),
            )

        val thirdPageResult =
            repository.findAll(UserSpecification(secondPageCursor, null), Pageable.ofSize(10))
        assertThat(thirdPageResult.hasNext()).isFalse
        assertThat(thirdPageResult.totalElements).isEqualTo(5)
        assertThat(thirdPageResult.content.size).isEqualTo(5)
    }

    @Test
    fun `filter users for role type`() {
        repository.saveAll(
            (1..5).map {
                UserEntity("user$it", "password")
            },
        )
        repository.saveAll(
            (1..5).map {
                UserEntity("admin$it", "password", roles = setOf(RoleEntity(RoleType.ADMIN)))
            },
        )

        val userFilter = repository.findAll(UserSpecification(null, RoleType.USER), Pageable.ofSize(10))

        assertThat(userFilter.hasNext()).isFalse()
        assertThat(userFilter.content.size).isEqualTo(5)

        val adminFilter = repository.findAll(UserSpecification(null, RoleType.ADMIN), Pageable.ofSize(10))

        assertThat(adminFilter.hasNext()).isFalse()
        assertThat(adminFilter.content.size).isEqualTo(5)
    }
}
