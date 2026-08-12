package com.docutesis.users.mappers

import com.docutesis.users.dtos.UserCreateRequest
import com.docutesis.users.dtos.UserResponse
import com.docutesis.users.entities.User
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class UserMapper {

    fun toEntity(request: UserCreateRequest): User {
        return User(
            cognitoId = request.cognitoId,
            email = request.email,
            fullName = request.fullName,
            role = request.role
        )
    }

    fun toResponse(entity: User): UserResponse {
        return UserResponse(
            id = entity.id ?: 0L,
            cognitoId = entity.cognitoId,
            email = entity.email,
            fullName = entity.fullName,
            role = entity.role ?: "STUDENT",
            createdAt = entity.createdAt ?: LocalDateTime.now()
        )
    }
}