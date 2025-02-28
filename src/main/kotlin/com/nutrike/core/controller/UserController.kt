package com.nutrike.core.controller

import com.nutrike.core.dto.AuthRequestDto
import com.nutrike.core.dto.AuthResponseDto
import com.nutrike.core.service.UserService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@Tag(name = "Authentication", description = "User authentication")
@RequestMapping("/user")
class UserController {
    @Autowired
    private lateinit var service: UserService

    @Operation(
        summary = "Authenticate a user",
        description =
            "Authenticate a user and retrieve a Jwt token to access" +
                " ressources of nutrike app",
        security = [],
    )
    @PostMapping("/token")
    fun getToken(
        @RequestBody authRequestDto: AuthRequestDto,
    ): ResponseEntity<AuthResponseDto> =
        ResponseEntity.ok(
            service.authenticateWithUsernameAndPassword(authRequestDto),
        )
}
