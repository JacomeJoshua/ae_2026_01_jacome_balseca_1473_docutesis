package com.docutesis.users.repositories

import com.docutesis.users.entities.User
import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository : JpaRepository<User, Long> {
    fun findByCognitoId(cognitoId: String): User?
    fun existsByCognitoId(cognitoId: String): Boolean
}