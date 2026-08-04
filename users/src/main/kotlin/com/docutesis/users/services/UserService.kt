package com.docutesis.users.services

import com.docutesis.users.dtos.UserRequest
import com.docutesis.users.dtos.UserResponse
import com.docutesis.users.exceptions.DuplicateCognitoIdException
import com.docutesis.users.exceptions.UserNotFoundException
import com.docutesis.users.mappers.UserMapper
import com.docutesis.users.repositories.UserRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserService(
    private val userRepository: UserRepository,
    private val userMapper: UserMapper
) {
    private val log = LoggerFactory.getLogger(UserService::class.java)

    @Transactional
    fun createUserProfile(cognitoId: String, request: UserRequest): UserResponse {
        if (userRepository.existsByCognitoId(cognitoId)) {
            log.warn("event=user.create.failed | msg=User profile already exists | cognitoId={}", cognitoId)
            throw DuplicateCognitoIdException("Profile already exists for this user")
        }

        val entity = userMapper.toEntity(request, cognitoId)
        val saved = userRepository.save(entity)
        log.info("event=user.created | msg=User profile created successfully | id={}", saved.id)
        return userMapper.toResponse(saved)
    }

    @Transactional(readOnly = true)
    fun getUserByCognitoId(cognitoId: String): UserResponse {
        val user = userRepository.findByCognitoId(cognitoId)
            ?: throw UserNotFoundException("User not found with cognitoId: $cognitoId").also {
                log.warn("event=user.not_found | msg=User profile not found | cognitoId={}", cognitoId)
            }
        return userMapper.toResponse(user)
    }

    @Transactional(readOnly = true)
    fun getUserById(id: Long): UserResponse {
        val user = userRepository.findById(id).orElseThrow {
            log.warn("event=user.not_found | msg=User not found | id={}", id)
            UserNotFoundException("User not found with id: $id")
        }
        return userMapper.toResponse(user)
    }

    @Transactional(readOnly = true)
    fun getAllUsers(): List<UserResponse> {
        return userRepository.findAll().map { userMapper.toResponse(it) }
    }

    @Transactional
    fun updateUserProfile(cognitoId: String, request: UserRequest): UserResponse {
        val user = userRepository.findByCognitoId(cognitoId)
            ?: throw UserNotFoundException("User profile not found")

        user.name = request.name
        user.email = request.email
        user.phone = request.phone

        val updated = userRepository.save(user)
        log.info("event=user.updated | msg=User profile updated | id={}", updated.id)
        return userMapper.toResponse(updated)
    }

    @Transactional
    fun deleteUser(id: Long) {
        if (!userRepository.existsById(id)) {
            throw UserNotFoundException("User not found with id: $id")
        }
        userRepository.deleteById(id)
        log.info("event=user.deleted | msg=User deleted | id={}", id)
    }
}