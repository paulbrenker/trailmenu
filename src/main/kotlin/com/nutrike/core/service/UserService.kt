package com.nutrike.core.service

import com.nutrike.core.dto.AuthRequestDto
import com.nutrike.core.dto.AuthResponseDto
import com.nutrike.core.repo.UserRepository
import com.nutrike.core.util.JwtUtil
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class UserService {
    @Autowired
    private lateinit var jwtUtil: JwtUtil

    @Autowired
    private lateinit var userRepository: UserRepository

    fun authenticateWithUsernameAndPassword(authRequest: AuthRequestDto): AuthResponseDto {
        val response =
            userRepository
                .findUserEntityByUsernameAndPasswordAndApprovalIsTrue(
                    authRequest.username,
                    authRequest.password,
                ).let { userEntity -> AuthResponseDto(jwtUtil.generateToken(userEntity!!.username)) }
        return response
    }
}
