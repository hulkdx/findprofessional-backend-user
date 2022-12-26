package com.hulkdx.findprofessional

import com.hulkdx.findprofessional.models.RegisterRequest
import com.hulkdx.findprofessional.models.User
import com.hulkdx.findprofessional.utils.TestPasswordEncoder
import com.hulkdx.findprofessional.utils.createRegisterRequest
import com.hulkdx.findprofessional.utils.errorMessage
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.whenever
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.http.HttpStatus
import org.springframework.security.crypto.password.PasswordEncoder

@OptIn(ExperimentalCoroutinesApi::class)
@ExtendWith(MockitoExtension::class)
class RegisterTests {

    private lateinit var sut: AuthController

    private lateinit var service: AuthService

    @Mock
    private lateinit var repository: UserRepository

    @Mock
    private lateinit var tokenService: TokenService

    private val passwordEncoder: PasswordEncoder = TestPasswordEncoder()

    @BeforeEach
    fun setup() {
        service = AuthService(repository, passwordEncoder)
        sut = AuthController(service, tokenService)
    }

    @Test
    fun `valid cases`() = runTest {
        // Arrange
        val email = "test@email.com"
        val password = "1234abdcx"
        val user = RegisterRequest(email, password)
        // Act
        val response = sut.register(user)
        // Assert
        assertEquals(HttpStatus.CREATED, response.statusCode)
        verify(repository).save(any())
    }

    @Test
    fun `when email exists then conflict`() = runTest {
        // Arrange
        val user = createRegisterRequest()
        emailExists()
        // Act
        val response = sut.register(user)
        // Assert
        assertEquals(HttpStatus.CONFLICT, response.statusCode)
        assertEquals("Email already exists", response.errorMessage)
    }

    @Test
    fun `when invalid email then bad request`() = runTest {
        val invalidEmails = listOf(
            "example.com",
            "not",
            "123",
            "email@",
            "email@exampl",
        )
        for (email in invalidEmails) {
            // Arrange
            val user = createRegisterRequest(email = email)
            // Act
            val response = sut.register(user)
            // Assert
            assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
        }
    }

    @Test
    fun `when invalid password then bad request`() = runTest {
        val invalidPassword = listOf(
            "1",
            "123a5v7",
            "123456789",
            "asdfghjkl",
            "!@#$%^&*(",
        )
        for (password in invalidPassword) {
            // Arrange
            val user = createRegisterRequest(password = password)
            // Act
            val response = sut.register(user)
            // Assert
            assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
        }
    }

    @Test
    fun `don't store raw password`() = runTest {
        // Arrange
        val user = createRegisterRequest()
        // Act
        sut.register(user)
        // Assert
        val ac = argumentCaptor<User>()
        verify(repository).save(ac.capture())

        val unexpected = user.password
        val actual = ac.firstValue.password
        assertNotEquals(unexpected, actual)
    }

    // region helpers

    private suspend fun emailExists() {
        whenever(repository.save(any()))
            .thenThrow(DataIntegrityViolationException(""))
    }

    // endregion
}
