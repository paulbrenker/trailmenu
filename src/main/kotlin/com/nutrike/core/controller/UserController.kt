package com.nutrike.core.controller

import com.nutrike.core.dto.UserAuthResponseDto
import com.nutrike.core.dto.UserPermissionsUpdateRequestDto
import com.nutrike.core.dto.UserRequestDto
import com.nutrike.core.dto.UserResponseDto
import com.nutrike.core.service.UserService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@Tag(name = "Authentication", description = "User authentication")
@RequestMapping("/user")
@Validated
class UserController {
    @Autowired
    internal lateinit var service: UserService

    @Operation(
        summary = "Authenticate a user",
        description =
            "Authenticate a user and retrieve a Jwt token to access" +
                " resources of nutrike app. No auth needed.",
    )
    @PostMapping("/token")
    fun getToken(
        @Valid @RequestBody userAuthRequestDto: UserRequestDto,
    ): ResponseEntity<UserAuthResponseDto> = service.authenticateWithUsernameAndPassword(userAuthRequestDto)

    @Operation(
        summary = "Find all Users",
        description = "List all registered Users, requires Admin permissions",
    )
    @GetMapping
    fun getAllUsers(): ResponseEntity<List<UserResponseDto>> = service.findAllUsers()

    @Operation(
        summary = "Add a new User",
        description =
            "Add a new user to the database. New users need to be approved by admins. Endpoint needs no " +
                "authentication",
    )
    @PostMapping
    fun insertUser(
        @Valid @RequestBody userInsert: UserRequestDto,
    ): ResponseEntity<UserResponseDto> = service.insertUser(userInsert)

    @Operation(
        summary = "Update a users permissions",
        description = "Update a users permission properties. Endpoint requires admin permissions.",
    )
    @PutMapping("/{username}/approval")
    fun updateUser(
        @Valid @PathVariable username: String,
        @Valid @RequestBody userUpdate: UserPermissionsUpdateRequestDto,
    ): ResponseEntity<UserResponseDto> = service.updateUser(username, userUpdate)
}
