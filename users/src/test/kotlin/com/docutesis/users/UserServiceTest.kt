package com.docutesis.users.services

import com.docutesis.users.dtos.UserRequest
import com.docutesis.users.dtos.UserResponse
import com.docutesis.users.entities.User
import com.docutesis.users.exceptions.DuplicateCognitoIdException
import com.docutesis.users.exceptions.UserNotFoundException
import com.docutesis.users.mappers.UserMapper
import com.docutesis.users.repositories.UserRepository
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension
import java.util.*

@ExtendWith(MockitoExtension::class)
class UserServiceTest {

    @Mock
    private lateinit var userRepository: UserRepository

    @Mock
    private lateinit var userMapper: UserMapper

    @InjectMocks
    private lateinit var userService: UserService

    private val sampleCognitoId = "a1b2c3d4-5678-90ab-cdef-1234567890ab"
    private val sampleRequest = UserRequest("Ana Lopez", "ana@puce.edu.ec", "0999999999")
    private val sampleEntity = User(1L, sampleCognitoId, "Ana Lopez", "ana@puce.edu.ec", "0999999999")
    private val sampleResponse = UserResponse(1L, sampleCognitoId, "Ana Lopez", "ana@puce.edu.ec", "0999999999")

    @Test
    fun `createUserProfile - success`() {
        `when`(userRepository.existsByCognitoId(sampleCognitoId)).thenReturn(false)
        `when`(userMapper.toEntity(sampleRequest, sampleCognitoId)).thenReturn(sampleEntity)
        `when`(userRepository.save(sampleEntity)).thenReturn(sampleEntity)
        `when`(userMapper.toResponse(sampleEntity)).thenReturn(sampleResponse)

        val result = userService.createUserProfile(sampleCognitoId, sampleRequest)

        assertNotNull(result)
        assertEquals(sampleCognitoId, result.cognitoId)
        assertEquals("Ana Lopez", result.name)
        verify(userRepository, times(1)).save(sampleEntity)
    }

    @Test
    fun `createUserProfile - throws DuplicateCognitoIdException when profile exists`() {
        `when`(userRepository.existsByCognitoId(sampleCognitoId)).thenReturn(true)

        assertThrows<DuplicateCognitoIdException> {
            userService.createUserProfile(sampleCognitoId, sampleRequest)
        }

        verify(userRepository, never()).save(any())
    }

    @Test
    fun `getUserByCognitoId - success`() {
        `when`(userRepository.findByCognitoId(sampleCognitoId)).thenReturn(sampleEntity)
        `when`(userMapper.toResponse(sampleEntity)).thenReturn(sampleResponse)

        val result = userService.getUserByCognitoId(sampleCognitoId)

        assertNotNull(result)
        assertEquals(sampleCognitoId, result.cognitoId)
    }

    @Test
    fun `getUserByCognitoId - throws UserNotFoundException when not found`() {
        `when`(userRepository.findByCognitoId(sampleCognitoId)).thenReturn(null)

        assertThrows<UserNotFoundException> {
            userService.getUserByCognitoId(sampleCognitoId)
        }
    }

    @Test
    fun `getUserById - success`() {
        `when`(userRepository.findById(1L)).thenReturn(Optional.of(sampleEntity))
        `when`(userMapper.toResponse(sampleEntity)).thenReturn(sampleResponse)

        val result = userService.getUserById(1L)

        assertNotNull(result)
        assertEquals(1L, result.id)
    }

    @Test
    fun `getUserById - throws UserNotFoundException when id does not exist`() {
        `when`(userRepository.findById(99L)).thenReturn(Optional.empty())

        assertThrows<UserNotFoundException> {
            userService.getUserById(99L)
        }
    }

    @Test
    fun `updateUserProfile - success`() {
        val updatedRequest = UserRequest("Ana Lopez Updated", "ana@puce.edu.ec", "0988888888")
        val updatedResponse = UserResponse(1L, sampleCognitoId, "Ana Lopez Updated", "ana@puce.edu.ec", "0988888888")

        `when`(userRepository.findByCognitoId(sampleCognitoId)).thenReturn(sampleEntity)
        `when`(userRepository.save(sampleEntity)).thenReturn(sampleEntity)
        `when`(userMapper.toResponse(sampleEntity)).thenReturn(updatedResponse)

        val result = userService.updateUserProfile(sampleCognitoId, updatedRequest)

        assertEquals("Ana Lopez Updated", result.name)
        verify(userRepository, times(1)).save(sampleEntity)
    }

    @Test
    fun `deleteUser - success`() {
        `when`(userRepository.existsById(1L)).thenReturn(true)

        userService.deleteUser(1L)

        verify(userRepository, times(1)).deleteById(1L)
    }
}