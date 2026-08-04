package com.docutesis.users.mappers

import com.docutesis.users.dtos.UserRequest
import com.docutesis.users.dtos.UserResponse
import com.docutesis.users.entities.User
import org.springframework.stereotype.Component

@Component
class UserMapper {

    fun toEntity(request: UserRequest, cognitoId: String): User {
        return User(
            cognitoId = cognitoId,
            name = request.name,
            email = request.email,
            phone = request.phone
        )
    }

    fun toResponse(entity: User): UserResponse {
        return UserResponse(
            id = entity.id ?: 0L,
            cognitoId = entity.cognitoId,
            name = entity.name,
            email = entity.email,
            phone = entity.phone
        )
    }
}