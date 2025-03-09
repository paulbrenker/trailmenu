package com.nutrike.core.service

import com.nutrike.core.dto.UserAuthResponseDto
import com.nutrike.core.dto.UserPermissionsUpdateRequestDto
import com.nutrike.core.dto.UserRequestDto
import com.nutrike.core.dto.UserResponseDto
import com.nutrike.core.entity.RoleEntity
import com.nutrike.core.entity.UserEntity
import com.nutrike.core.repo.UserRepository
import com.nutrike.core.util.JwtUtil
import com.nutrike.core.util.PasswordEncoderUtil
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service

@Service
class UserService {
    @Autowired
    internal lateinit var jwtUtil: JwtUtil

    @Autowired
    internal lateinit var userRepository: UserRepository

    @Autowired
    internal lateinit var passwordEncoder: PasswordEncoderUtil

    fun authenticateWithUsernameAndPassword(authRequest: UserRequestDto): ResponseEntity<UserAuthResponseDto> {
        val userEntity =
            userRepository.findUserEntityByUsernameAndApprovalIsTrue(
                authRequest.username,
            )

        return if (
            userEntity != null &&
            passwordEncoder.verifyPassword(authRequest.password, userEntity.password)
        ) {
            ResponseEntity.ok(
                UserAuthResponseDto(
                    jwtUtil.generateToken(
                        userEntity.username,
                        userEntity.roles
                            .map {
                                it.type.toString()
                            }.toSet(),
                    ),
                ),
            )
        } else {
            ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
        }
    }

    fun findAllUsers(): ResponseEntity<List<UserResponseDto>> {
        val userEntities = userRepository.findAll()
        return if (userEntities.isNotEmpty()) {
            ResponseEntity.ok(
                userEntities.map {
                    entityToResponseDto(it)
                },
            )
        } else {
            ResponseEntity.status(HttpStatus.NOT_FOUND).build()
        }
    }

    fun insertUser(userRequestDto: UserRequestDto): ResponseEntity<UserResponseDto> =
        try {
            ResponseEntity.ok(
                entityToResponseDto(
                    userRepository
                        .save(
                            UserEntity(
                                userRequestDto.username,
                                passwordEncoder.encodePassword(userRequestDto.password),
                            ),
                        ),
                ),
            )
        } catch (e: Exception) {
            println(e.message)
            ResponseEntity.status(HttpStatus.BAD_REQUEST).build()
        }

    fun updateUser(
        username: String,
        userUpdateRequestDto: UserPermissionsUpdateRequestDto,
    ): ResponseEntity<UserResponseDto> {
        val userEntity =
            userRepository
                .findById(username)

        if (userEntity.isEmpty) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build()
        }

        val updatedUser =
            userEntity.get().copy(
                approval = userUpdateRequestDto.approval,
                roles = userUpdateRequestDto.roles.map { RoleEntity(it.type) }.toSet(),
            )

        val savedUser = userRepository.save(updatedUser)
        return ResponseEntity.ok(entityToResponseDto(savedUser))
    }

    private fun entityToResponseDto(entity: UserEntity) =
        UserResponseDto(
            entity.username,
            entity.approval,
            entity.roles,
        )
}
