package com.docutesis.users.controllers

import com.docutesis.users.dtos.UserRequest
import com.docutesis.users.dtos.UserResponse
import com.docutesis.users.services.UserService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/users")
class UserController(
    private val userService: UserService
) {

    @PostMapping("/me")
    @ResponseStatus(HttpStatus.CREATED)
    fun createMyProfile(
        @AuthenticationPrincipal jwt: Jwt,
        @Valid @RequestBody request: UserRequest
    ): UserResponse {
        val cognitoId = jwt.subject ?: throw IllegalArgumentException("JWT subject cannot be null")
        return userService.createUserProfile(cognitoId, request)
    }

    @GetMapping("/me")
    fun getMyProfile(
        @AuthenticationPrincipal jwt: Jwt
    ): UserResponse {
        val cognitoId = jwt.subject ?: throw IllegalArgumentException("JWT subject cannot be null")
        return userService.getUserByCognitoId(cognitoId)
    }

    @PutMapping("/me")
    fun updateMyProfile(
        @AuthenticationPrincipal jwt: Jwt,
        @Valid @RequestBody request: UserRequest
    ): UserResponse {
        val cognitoId = jwt.subject ?: throw IllegalArgumentException("JWT subject cannot be null")
        return userService.updateUserProfile(cognitoId, request)
    }

    @GetMapping("/cognito/{cognitoId}")
    fun getUserByCognitoId(@PathVariable cognitoId: String): UserResponse {
        return userService.getUserByCognitoId(cognitoId)
    }

    @GetMapping
    fun getAllUsers(): List<UserResponse> {
        return userService.getAllUsers()
    }

    @GetMapping("/{id}")
    fun getUserById(@PathVariable id: Long): UserResponse {
        return userService.getUserById(id)
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteUser(@PathVariable id: Long) {
        userService.deleteUser(id)
    }
}