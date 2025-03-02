package com.nutrike.core.service

import com.nutrike.core.dto.UserAuthResponseDto
import com.nutrike.core.dto.UserRequestDto
import com.nutrike.core.dto.UserResponseDto
import com.nutrike.core.dto.UserUpdateRequestDto
import com.nutrike.core.entity.RoleEntity
import com.nutrike.core.entity.RoleType
import com.nutrike.core.entity.UserEntity
import com.nutrike.core.repo.RoleRepository
import com.nutrike.core.repo.UserRepository
import com.nutrike.core.util.JwtUtil
import jakarta.persistence.EntityNotFoundException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class UserService {
    @Autowired
    private lateinit var jwtUtil: JwtUtil

    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    private lateinit var roleRepository: RoleRepository

    fun authenticateWithUsernameAndPassword(authRequest: UserRequestDto): ResponseEntity<UserAuthResponseDto> {
        val userEntity =
            userRepository.findUserEntityByUsernameAndPasswordAndApprovalIsTrue(
                authRequest.username,
                authRequest.password,
            )

        return if (userEntity != null) {
            ResponseEntity.ok(UserAuthResponseDto(jwtUtil.generateToken(userEntity.username)))
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
            ResponseEntity.status(HttpStatus.NO_CONTENT).build()
        }
    }

    fun insertUser(userRequestDto: UserRequestDto): ResponseEntity<UserResponseDto> =
        try {
            ResponseEntity.ok(
                entityToResponseDto(
                    userRepository
                        .save(
                            requestDtoToEntity(userRequestDto),
                        ),
                ),
            )
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.BAD_REQUEST).build()
        }

    fun updateUser(
        id: UUID,
        userUpdateRequestDto: UserUpdateRequestDto,
    ): ResponseEntity<UserResponseDto> {
        val userEntity =
            userRepository
                .findById(id)
                .orElseThrow { EntityNotFoundException("User with id $id not found") }

        if (userEntity.password != userUpdateRequestDto.oldPassword) {
            throw IllegalArgumentException("Old password is incorrect")
        }

        val updatedUser =
            userEntity.copy(
                username = userUpdateRequestDto.username,
                password = userUpdateRequestDto.newPassword,
                approval = userUpdateRequestDto.approval,
                roles = getRoles(userUpdateRequestDto.roles.map { it.type }.toSet()),
            )

        val savedUser = userRepository.save(updatedUser)
        return ResponseEntity.ok(entityToResponseDto(savedUser))
    }

    private fun getRoles(roles: Set<RoleType>): Set<RoleEntity> =
        roles
            .map {
                roleRepository.findById(it).get()
            }.toSet()

    private fun entityToResponseDto(entity: UserEntity) =
        UserResponseDto(
            entity.id!!,
            entity.username,
            entity.approval,
            entity.roles,
        )

    private fun requestDtoToEntity(requestDto: UserRequestDto): UserEntity =
        UserEntity(
            username = requestDto.username,
            password = requestDto.password,
            roles = getRoles(setOf(RoleType.USER)),
        )
}
