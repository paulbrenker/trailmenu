package com.nutrike.core.service

import com.nutrike.core.dto.PageDto
import com.nutrike.core.dto.PageInfoDto
import com.nutrike.core.dto.UserAuthResponseDto
import com.nutrike.core.dto.UserPermissionsPatchRequestDto
import com.nutrike.core.dto.UserRequestDto
import com.nutrike.core.dto.UserResponseDto
import com.nutrike.core.entity.RoleEntity
import com.nutrike.core.entity.RoleType
import com.nutrike.core.entity.UserEntity
import com.nutrike.core.repo.UserRepository
import com.nutrike.core.repo.UserSpecification
import com.nutrike.core.util.CursorUtil
import com.nutrike.core.util.JwtUtil
import com.nutrike.core.util.PasswordEncoderUtil
import jakarta.transaction.Transactional
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Pageable
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
            userRepository.findUserEntityByUsername(
                authRequest.username,
            )

        return if (
            userEntity != null &&
            passwordEncoder.verifyPassword(authRequest.password, userEntity.password) &&
            !userEntity.roles.contains(RoleEntity(RoleType.PENDING))
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

    @Transactional
    fun findAllUsers(
        roleType: RoleType?,
        limit: Int,
        cursor: String?,
    ): ResponseEntity<PageDto<UserResponseDto>> =
        userRepository.findAll(UserSpecification(cursor, roleType), Pageable.ofSize(limit)).let {
            ResponseEntity.ok(
                PageDto<UserResponseDto>(
                    pageInfo =
                        PageInfoDto(
                            pageSize = it.content.size,
                            hasNext = it.hasNext(),
                            endCursor =
                                it.content.lastOrNull()?.username?.let { username ->
                                    CursorUtil.getEncodedCursor(mapOf("username" to username), it.hasNext())
                                },
                        ),
                    totalCount = userRepository.count().toInt(),
                    data = it.content.map(::entityToResponseDto),
                ),
            )
        }

    fun insertUser(userRequestDto: UserRequestDto): ResponseEntity<UserResponseDto> =
        try {
            if (userRepository.existsById(userRequestDto.username)) {
                ResponseEntity.status(HttpStatus.BAD_REQUEST).build()
            } else {
                val savedUser =
                    userRepository.save(
                        UserEntity(
                            userRequestDto.username,
                            passwordEncoder.encodePassword(userRequestDto.password),
                        ),
                    )
                ResponseEntity.ok(entityToResponseDto(savedUser))
            }
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.BAD_REQUEST).build()
        }

    fun patchUser(
        username: String,
        userUpdateRequestDto: UserPermissionsPatchRequestDto,
    ): ResponseEntity<UserResponseDto> {
        val userEntity =
            userRepository
                .findById(username)

        if (userEntity.isEmpty) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build()
        }

        val updatedUser =
            userEntity.get().copy(
                roles = userUpdateRequestDto.roles.map { RoleEntity(it.type) }.toSet(),
            )

        val savedUser = userRepository.save(updatedUser)
        return ResponseEntity.ok(entityToResponseDto(savedUser))
    }

    private fun entityToResponseDto(entity: UserEntity) =
        UserResponseDto(
            entity.username,
            entity.roles,
        )
}
