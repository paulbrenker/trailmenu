package com.nutrike.core.service

import com.nutrike.core.dto.UserPermissionsUpdateRequestDto
import com.nutrike.core.dto.UserRequestDto
import com.nutrike.core.entity.RoleEntity
import com.nutrike.core.entity.RoleType
import com.nutrike.core.entity.UserEntity
import com.nutrike.core.repo.RoleRepository
import com.nutrike.core.repo.UserRepository
import com.nutrike.core.util.JwtUtil
import io.mockk.every
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.hibernate.exception.ConstraintViolationException
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.http.HttpStatus
import java.sql.SQLException
import java.util.Optional

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
        val mockUser =
            UserEntity(
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

    @Test
    fun `find all users returns not found if no entries in db`() {
        every { userRepository.findAll() } returns emptyList()
        val response = service.findAllUsers()
        assertThat(response.statusCode).isEqualTo(HttpStatus.NOT_FOUND)
    }

    @Test
    fun `find all users returns ok if at least one user is in db`() {
        every { userRepository.findAll() } returns
            listOf(
                UserEntity("username", "password"),
            )
        val response = service.findAllUsers()
        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
    }

    @Test
    fun `insert user throws an error when the repository returns an error`() {
        every { roleRepository.findById(RoleType.USER) } returns Optional.of(RoleEntity(RoleType.USER))
        every { userRepository.save(any()) } throws ConstraintViolationException("sql violation", SQLException(), null)
        val response =
            service.insertUser(
                UserRequestDto("constraintViolatingUsername", "password"),
            )
        assertThat(response.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)
    }

    @Test
    fun `insert user returns ok when repository returns an Entity`() {
        every { roleRepository.findById(RoleType.USER) } returns
            Optional.of(RoleEntity(RoleType.USER))
        every { userRepository.save(any()) } returns
            UserEntity("username", "password")
        val response =
            service.insertUser(
                UserRequestDto("username", "password"),
            )
        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
    }

    @Test
    fun `update a user that does not exist returns not found response`() {
        every { userRepository.findById(any()) } returns Optional.empty()
        val response =
            service.updateUser(
                "non-exist-user",
                UserPermissionsUpdateRequestDto(
                    true,
                    listOf(RoleEntity(RoleType.USER)),
                ),
            )
        assertThat(response.statusCode).isEqualTo(HttpStatus.NOT_FOUND)
    }

    @Test
    fun `updateUser returns ok response if successfully updated`() {
        every { roleRepository.findById(RoleType.USER) } returns
            Optional.of(RoleEntity(RoleType.USER))
        every { userRepository.findById(any()) } returns
            Optional.of(UserEntity("username", "password"))
        every { userRepository.save(any()) } returns
            UserEntity("username", "password", true)
        val response =
            service.updateUser(
                "username",
                UserPermissionsUpdateRequestDto(
                    true,
                    listOf(RoleEntity(RoleType.USER)),
                ),
            )
        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(response.body!!.approval).isTrue
    }
}
