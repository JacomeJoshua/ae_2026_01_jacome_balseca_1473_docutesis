package com.docutesis.users.dtos

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank

data class UserRequest(
    @field:NotBlank(message = "Name is required")
    val name: String,

    @field:NotBlank(message = "Email is required")
    @field:Email(message = "Invalid email format")
    val email: String,

    val phone: String? = null
)

data class UserResponse(
    val id: Long,
    val cognitoId: String,
    val name: String,
    val email: String,
    val phone: String?
)