package com.nutrike.core.service

import com.nutrike.core.dto.UserRequestDto
import com.nutrike.core.entity.UserEntity
import com.nutrike.core.repo.RoleRepository
import com.nutrike.core.repo.UserRepository
import com.nutrike.core.util.JwtUtil
import io.mockk.every
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.http.HttpStatus
import java.util.UUID

@ExtendWith(MockKExtension::class)
class UserServiceTest {
    private val userRepository: UserRepository = mockk()
    private val jwtUtil: JwtUtil = mockk()
    private val roleRepository: RoleRepository = mockk()

    private val service = UserService()

    init {
        service.jwtUtil = jwtUtil
        service.userRepository = userRepository
        service.roleRepository = roleRepository
    }

    @Test
    fun `authentication should return unauthorized when username password not found`() {
        val nonExistUsername = "not-existent"
        val nonMatchingPassword = "1234"

        every {
            userRepository.findUserEntityByUsernameAndPasswordAndApprovalIsTrue(nonExistUsername, nonMatchingPassword)
        } returns null

        assertThat(
            service
                .authenticateWithUsernameAndPassword(
                    UserRequestDto(nonExistUsername, nonMatchingPassword),
                ).statusCode,
        ).isEqualTo(HttpStatus.UNAUTHORIZED)
    }

    @Test
    fun `authentication should return ok when username and password succeeds`() {
        val existUsername = "existent"
        val matchingPassword = "1234"
        val userId = UUID.randomUUID()
        val mockUser =
            UserEntity(
                id = userId,
                username = existUsername,
                password = matchingPassword,
                approval = true,
                roles = emptySet(),
            )
        val mockToken = "mockToken"

        every {
            userRepository.findUserEntityByUsernameAndPasswordAndApprovalIsTrue(existUsername, matchingPassword)
        } returns mockUser

        every { jwtUtil.generateToken(existUsername, emptySet()) } returns mockToken

        assertThat(
            service
                .authenticateWithUsernameAndPassword(
                    UserRequestDto(existUsername, matchingPassword),
                ).statusCode,
        ).isEqualTo(HttpStatus.OK)
    }
}
