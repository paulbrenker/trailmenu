package com.nutrike.core.controller

import com.nutrike.core.dto.UserAuthResponseDto
import com.nutrike.core.dto.UserResponseDto
import com.nutrike.core.entity.RoleEntity
import com.nutrike.core.entity.RoleType
import com.nutrike.core.service.UserService
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import java.util.stream.Stream

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserControllerTest {
    private var mockMvc: MockMvc
    private val userService: UserService = mockk()
    private var userController: UserController = UserController()

    init {
        userController.service = userService
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build()
    }

    @Test
    fun `test getToken`() {
        val responseDto = UserAuthResponseDto("jwt-token")
        every { userService.authenticateWithUsernameAndPassword(any()) } returns ResponseEntity.ok(responseDto)

        mockMvc
            .perform(
                post("/user/token")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        """
                        {
                            "username": "testuser",
                            "password": "password"
                        }
                        """.trimIndent(),
                    ),
            ).andExpect(status().isOk)
            .andExpect(jsonPath("$.token").value("jwt-token"))

        verify { userService.authenticateWithUsernameAndPassword(any()) }
    }

    private fun invalidAuthRequestDtoProvider() =
        Stream.of(
            Arguments.of(
                """
                {
                    "username": "",
                    "password": "",
                }
                """.trimIndent(),
            ),
            Arguments.of(
                """
                {
                    "username": "",
                    "password": "empty username",
                }
                """.trimIndent(),
            ),
            Arguments.of(
                """
                {
                    "username": "empty password",
                    "password": "",
                }
                """.trimIndent(),
            ),
            Arguments.of(
                """
                {
                    "password": "",
                }
                """.trimIndent(),
            ),
            Arguments.of(
                """
                {
                    "username": "",
                }
                """.trimIndent(),
            ),
            Arguments.of(
                """
                {
                    "username": null,
                    "password": null,
                }
                """.trimIndent(),
            ),
        )

    @ParameterizedTest
    @MethodSource("invalidAuthRequestDtoProvider")
    fun `invalid combinations in UserAuthRequestDto trigger a 400`(jsonContent: String) {
        mockMvc
            .perform(
                post("/user/token")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(jsonContent),
            ).andExpect(status().isBadRequest)
    }

    @Test
    fun `test insertUser`() {
        val responseDto =
            UserResponseDto(
                "test-user",
                false,
                setOf(RoleEntity(RoleType.USER)),
            )
        every { userService.insertUser(any()) } returns ResponseEntity.ok(responseDto)
        mockMvc
            .perform(
                post("/user")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        """
                        {
                            "username": "test-user",
                            "password": "1234"
                        }
                        """.trimIndent(),
                    ),
            ).andExpect(status().isOk)
            .andExpect(jsonPath("$.username").value("test-user"))
            .andExpect(jsonPath("$.approval").value(false))
        verify { userService.insertUser(any()) }
    }

    @Test
    fun `test patchUser endpoint returns NOT_FOUND on service not found`() {
        every { userService.patchUser(any(), any()) } returns ResponseEntity.notFound().build()
        mockMvc
            .perform(
                patch("/user/example/approval")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        """
                        {
                          "approval": true,
                          "roles": [
                            {
                              "type": "USER"
                            }
                          ]
                        }
                        """.trimIndent(),
                    ),
            ).andExpect(status().isNotFound)
    }
}
