package com.nutrike.core.controller

import com.nutrike.core.dto.AuthRequestDto
import com.nutrike.core.dto.AuthResponseDto
import com.nutrike.core.util.JwtUtil
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController()
@Tag(name = "Authentication", description = "User authentication")
class AuthenticationController(
    private val jwtUtil: JwtUtil,
) {
    @Operation(
        summary = "Authenticate a user",
        description =
            "Authenticate a user and retrieve a Jwt token to access" +
                " ressources of nutrike app",
        security = [],
    )
    @PostMapping("/auth")
    fun getToken(
        @RequestBody authRequestDto: AuthRequestDto,
    ): ResponseEntity<AuthResponseDto> =
        if (authRequestDto.username == "paulbrenker" && authRequestDto.password == "1234") {
            ResponseEntity.ok(AuthResponseDto(jwtUtil.generateToken(authRequestDto.username)))
        } else {
            ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
        }
}
