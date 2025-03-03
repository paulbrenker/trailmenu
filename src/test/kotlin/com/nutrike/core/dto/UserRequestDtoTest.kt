package com.nutrike.core.dto

import com.nutrike.core.controller.UserController
import jakarta.validation.ConstraintViolationException
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.util.stream.Stream

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserRequestDtoTest {
    @Autowired
    private lateinit var controller: UserController

    private fun invalidUsernameAndPasswordProvider() =
        Stream.of(
            Arguments.of("", "valid"),
            Arguments.of("x".repeat(151), "valid"),
            Arguments.of("valid", ""),
            Arguments.of("valid", "x".repeat(256)),
            Arguments.of("", ""),
        )

    @ParameterizedTest
    @MethodSource("invalidUsernameAndPasswordProvider")
    fun `invalid username combinations should fail`(
        username: String,
        password: String,
    ) {
        assertThrows<ConstraintViolationException> {
            controller.getToken(UserRequestDto("", ""))
        }
    }
}
