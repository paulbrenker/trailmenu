package com.nutrike.core.service

import com.nutrike.core.dto.UserPermissionsPatchRequestDto
import com.nutrike.core.dto.UserRequestDto
import com.nutrike.core.dto.UserResponseDto
import com.nutrike.core.entity.RoleEntity
import com.nutrike.core.entity.RoleType
import com.nutrike.core.entity.UserEntity
import com.nutrike.core.repo.UserRepository
import com.nutrike.core.util.JwtUtil
import com.nutrike.core.util.PasswordEncoderUtil
import io.mockk.every
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException
import java.util.Optional

@ExtendWith(MockKExtension::class)
class UserServiceTest {
    private val userRepository: UserRepository = mockk()
    private val jwtUtil: JwtUtil = mockk()
    private val passwordEncoder: PasswordEncoderUtil = mockk()

    private val service = UserService()

    init {
        service.jwtUtil = jwtUtil
        service.userRepository = userRepository
        service.passwordEncoder = passwordEncoder
    }

    @Test
    fun `authentication should return unauthorized when username password not found`() {
        val nonExistUsername = "not-existent"
        val nonMatchingPassword = "1234"

        every {
            userRepository.findUserEntityByUsername(nonExistUsername)
        } returns null

        val exception =
            assertThrows<ResponseStatusException> {
                service
                    .authenticateWithUsernameAndPassword(
                        UserRequestDto(nonExistUsername, nonMatchingPassword),
                    )
            }
        assertThat(exception.statusCode).isEqualTo(HttpStatus.UNAUTHORIZED)
        assertThat(exception.message).contains("username or password invalid")

        verify(inverse = true) { passwordEncoder.verifyPassword(any(), any()) }
    }

    @Test
    fun `authentication should return unauthorized when password does not match`() {
        val existUsername = "existent"
        val nonMatchingPassword = "1234"

        every {
            userRepository.findUserEntityByUsername(existUsername)
        } returns UserEntity("existent", "12345", setOf(RoleEntity(RoleType.USER)))
        every {
            passwordEncoder.verifyPassword(nonMatchingPassword, "12345")
        } returns false

        val exception =
            assertThrows<ResponseStatusException> {
                service
                    .authenticateWithUsernameAndPassword(
                        UserRequestDto(existUsername, nonMatchingPassword),
                    )
            }

        assertThat(exception.statusCode).isEqualTo(HttpStatus.UNAUTHORIZED)
        assertThat(exception.message).contains("username or password invalid")

        verify { passwordEncoder.verifyPassword(any(), any()) }
    }

    @Test
    fun `authentication should return forbidden when user is PENDING`() {
        val existUsername = "existent"
        val nonMatchingPassword = "1234"

        every {
            userRepository.findUserEntityByUsername(existUsername)
        } returns UserEntity("existent", "1234", setOf(RoleEntity(RoleType.PENDING)))
        every {
            passwordEncoder.verifyPassword(nonMatchingPassword, "1234")
        } returns true

        val exception =
            assertThrows<ResponseStatusException> {
                service
                    .authenticateWithUsernameAndPassword(
                        UserRequestDto(existUsername, nonMatchingPassword),
                    )
            }

        assertThat(exception.statusCode).isEqualTo(HttpStatus.FORBIDDEN)
        assertThat(exception.message).contains("Your account needs approval by an admin")

        verify { passwordEncoder.verifyPassword(any(), any()) }
    }

    @Test
    fun `authentication should return ok when username and password succeeds`() {
        val existUsername = "existent"
        val matchingPassword = "1234"
        val mockUser =
            UserEntity(
                username = existUsername,
                password = matchingPassword,
                roles = emptySet(),
            )
        val mockToken = "mockToken"

        every {
            userRepository.findUserEntityByUsername(existUsername)
        } returns mockUser

        every { jwtUtil.generateToken(existUsername, emptySet()) } returns mockToken
        every { passwordEncoder.verifyPassword("1234", matchingPassword) } returns true

        assertThat(
            service
                .authenticateWithUsernameAndPassword(
                    UserRequestDto(existUsername, matchingPassword),
                ).statusCode,
        ).isEqualTo(HttpStatus.OK)

        verify { passwordEncoder.verifyPassword("1234", matchingPassword) }
    }

    @Test
    fun `find all users returns empty data if no entries in db`() {
        val mockPage = mockk<Page<UserEntity>>(relaxed = true)

        every { userRepository.findAll(any(), Pageable.ofSize(10)) } returns mockPage
        every { userRepository.count() } returns 0
        every { mockPage.content.size } returns 0
        every { mockPage.hasNext() } returns false
        every { mockPage.content[any()] } returns null

        val response = service.findAllUsers(null, 10, null)

        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(response.body!!.data).isEmpty()
        assertThat(response.body!!.pageInfo.hasNext).isFalse()
        assertThat(response.body!!.pageInfo.pageSize).isEqualTo(0)
        assertThat(response.body!!.pageInfo.endCursor).isNull()
        assertThat(response.body!!.totalCount).isEqualTo(0)
    }

    @Test
    fun `find all users returns ok if at least one user is in db`() {
        val mockPage = mockk<Page<UserEntity>>(relaxed = true)

        every { userRepository.findAll(any(), Pageable.ofSize(10)) } returns mockPage
        every { userRepository.count() } returns 1
        every { mockPage.content.size } returns 1
        every { mockPage.hasNext() } returns false
        every { mockPage.content } returns
            listOf(
                UserEntity("username", "password"),
            )

        val response = service.findAllUsers(null, 10, null)

        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(response.body!!.data).containsExactly(
            UserResponseDto(
                "username",
                setOf(RoleEntity(RoleType.PENDING)),
            ),
        )
        assertThat(response.body!!.pageInfo.hasNext).isFalse()
        assertThat(response.body!!.pageInfo.pageSize).isEqualTo(1)
        assertThat(response.body!!.pageInfo.endCursor).isNull()
        assertThat(response.body!!.totalCount).isEqualTo(1)
    }

    @Test
    fun `insert user throws bad request error when user exists`() {
        every { userRepository.existsById(any()) } returns true

        val exception =
            assertThrows<ResponseStatusException> {
                service.insertUser(
                    UserRequestDto("constraintViolatingUsername", "password"),
                )
            }
        assertThat(exception.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)
        assertThat(exception.message).contains("the user already exists")

        verify(inverse = true) { passwordEncoder.encodePassword("password") }
    }

    @Test
    fun `insertUser returns ok when repository returns an Entity`() {
        every { userRepository.existsById(any()) } returns false
        every { userRepository.save(any()) } returns
            UserEntity("username", "password")
        every { passwordEncoder.encodePassword("password") } returns "encodedPassword"
        val response =
            service.insertUser(
                UserRequestDto("username", "password"),
            )
        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        verify { passwordEncoder.encodePassword("password") }
    }

    @Test
    fun `update a user that does not exist returns not found response`() {
        every { userRepository.findById(any()) } returns Optional.empty()
        val exception =
            assertThrows<ResponseStatusException> {
                service.patchUser(
                    "non-exist-user",
                    UserPermissionsPatchRequestDto(
                        listOf(RoleEntity(RoleType.USER)),
                    ),
                )
            }
        assertThat(exception.statusCode).isEqualTo(HttpStatus.NOT_FOUND)
        assertThat(exception.message).contains("User was not found")
    }

    @Test
    fun `updateUser returns ok response if successfully updated`() {
        every { userRepository.findById(any()) } returns
            Optional.of(UserEntity("username", "password"))
        every { userRepository.save(any()) } returns
            UserEntity("username", "password")
        val response =
            service.patchUser(
                "username",
                UserPermissionsPatchRequestDto(
                    listOf(RoleEntity(RoleType.USER)),
                ),
            )
        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
    }
}
