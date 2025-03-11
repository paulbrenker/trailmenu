package com.nutrike.core.repo

import com.nutrike.core.entity.UserEntity
import com.nutrike.core.util.BaseContainerTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class UserRepositoryTest : BaseContainerTest() {
    @Autowired
    private lateinit var repository: UserRepository

    private lateinit var user: UserEntity

    @BeforeEach
    fun setup() {
        user =
            repository.save(
                UserEntity(username = "test-user", password = "abc"),
            )
    }

    @AfterEach
    fun clean() {
        repository.deleteAll()
    }

    @Test
    fun `find by username and password and approve returns only all are true`() {
        val wrongUsername = repository.findUserEntityByUsername("non-existent")
        assertThat(wrongUsername).isNull()

        val rightCombination = repository.findUserEntityByUsername("test-user")
        assertThat(rightCombination).isNotNull
        assertThat(rightCombination).isEqualTo(user)
    }
}
