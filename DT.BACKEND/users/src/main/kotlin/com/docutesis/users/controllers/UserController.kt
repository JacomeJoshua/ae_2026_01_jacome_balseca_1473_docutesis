package com.docutesis.users.controllers

import com.docutesis.users.dtos.UserCreateRequest
import com.docutesis.users.dtos.UserResponse
import com.docutesis.users.services.UserService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping
class UserController(
    private val userService: UserService
) {

    @PostMapping
    fun createUserProfile(@RequestBody request: UserCreateRequest): ResponseEntity<UserResponse> {
        val response = userService.createUserProfile(request)
        return ResponseEntity.status(HttpStatus.CREATED).body(response)
    }

    @GetMapping("/me")
    fun getMyProfile(authentication: Authentication): ResponseEntity<UserResponse> {
        val cognitoId = authentication.name // Extrae el 'sub' del JWT
        val response = userService.getUserByCognitoId(cognitoId)
        return ResponseEntity.ok(response)
    }

    @GetMapping("/{cognitoId}")
    fun getUserProfile(@PathVariable cognitoId: String): ResponseEntity<UserResponse> {
        val response = userService.getUserByCognitoId(cognitoId)
        return ResponseEntity.ok(response)
    }
}