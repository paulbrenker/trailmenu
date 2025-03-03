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
                UserEntity(username = "test-user", password = "abc", approval = true),
            )
    }

    @AfterEach
    fun clean() {
        repository.deleteAll()
    }

    @Test
    fun `find by username and password and approve returns only all are true`() {
        val wrongUsername = repository.findUserEntityByUsernameAndPasswordAndApprovalIsTrue("non-existent", "abc")
        assertThat(wrongUsername).isNull()

        val wrongPassword = repository.findUserEntityByUsernameAndPasswordAndApprovalIsTrue("test-user", "123")
        assertThat(wrongPassword).isNull()

        val rightCombination = repository.findUserEntityByUsernameAndPasswordAndApprovalIsTrue("test-user", "abc")
        assertThat(rightCombination).isNotNull
        assertThat(rightCombination).isEqualTo(user)
    }
}
