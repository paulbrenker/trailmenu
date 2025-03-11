package com.nutrike.core.entity

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.validation.annotation.Validated

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Validated
class UserEntityTest {
    @Test
    fun `new user default parameters are set`() {
        val user = UserEntity(username = "testUser", password = "testPassword")

        assertThat(user.username).isEqualTo(user.username)
        assertThat(user.password).isEqualTo(user.password)
        assertThat(user.roles).hasSize(1)
        assertThat(user.roles).containsOnly(RoleEntity(type = RoleType.PENDING))
    }
}
