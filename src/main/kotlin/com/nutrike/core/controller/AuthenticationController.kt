package com.nutrike.core.controller

import com.nutrike.core.dto.AuthRequestDto
import com.nutrike.core.dto.AuthResponseDto
import com.nutrike.core.util.JwtUtil
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController()
class AuthenticationController(
    private val jwtUtil: JwtUtil,
) {
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
