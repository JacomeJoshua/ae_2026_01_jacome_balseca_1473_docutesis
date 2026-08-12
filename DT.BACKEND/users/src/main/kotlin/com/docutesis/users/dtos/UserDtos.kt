package com.docutesis.users.dtos

import java.time.LocalDateTime

data class UserCreateRequest(
    val cognitoId: String,
    val email: String,
    val fullName: String,
    val role: String
)

data class UserResponse(
    val id: Long,
    val cognitoId: String,
    val email: String,
    val fullName: String,
    val role: String,
    val createdAt: LocalDateTime
)