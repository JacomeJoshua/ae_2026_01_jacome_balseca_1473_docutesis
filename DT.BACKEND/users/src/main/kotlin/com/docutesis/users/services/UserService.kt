package com.docutesis.users.services

import com.docutesis.users.dtos.UserCreateRequest
import com.docutesis.users.dtos.UserResponse
import com.docutesis.users.exceptions.UserNotFoundException
import com.docutesis.users.mappers.UserMapper
import com.docutesis.users.repositories.UserRepository
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserService(
    private val userRepository: UserRepository,
    private val userMapper: UserMapper
) {

    @Transactional
    fun createUserProfile(request: UserCreateRequest): UserResponse {
        val existingUser = userRepository.findByCognitoId(request.cognitoId)

        val user = if (existingUser.isPresent) {
            val existing = existingUser.get()
            existing.email = request.email
            existing.fullName = request.fullName
            existing.role = request.role
            userRepository.save(existing)
        } else {
            try {
                val newUser = userMapper.toEntity(request)
                userRepository.save(newUser)
            } catch (e: DataIntegrityViolationException) {
                userRepository.findByCognitoId(request.cognitoId)
                    .orElseThrow { e }
            }
        }

        return userMapper.toResponse(user)
    }

    @Transactional(readOnly = true)
    fun getUserByCognitoId(cognitoId: String): UserResponse {
        val user = userRepository.findByCognitoId(cognitoId)
            .orElseThrow { UserNotFoundException("User profile not found for cognitoId: $cognitoId") }
        return userMapper.toResponse(user)
    }
}