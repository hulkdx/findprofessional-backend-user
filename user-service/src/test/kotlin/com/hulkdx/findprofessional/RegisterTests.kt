package com.hulkdx.findprofessional

import com.hulkdx.findprofessional.models.AuthRequest
import com.hulkdx.findprofessional.models.TokenResponse
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
import org.mockito.kotlin.anyOrNull
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.http.HttpStatus

@OptIn(ExperimentalCoroutinesApi::class)
@ExtendWith(MockitoExtension::class)
class RegisterTests {

    private lateinit var sut: AuthController

    @Mock
    private lateinit var repository: UserRepository

    @Mock
    private lateinit var tokenService: TokenService

    @BeforeEach
    fun setup() {
        val service = AuthService(repository, TestPasswordEncoder())
        sut = AuthController(service, tokenService, mock {})
    }

    @Test
    fun `valid cases`() = runTest {
        // Arrange
        val email = "test@email.com"
        val password = "1234abdcx"
        val user = AuthRequest(email, password)
        val expectedBody = TokenResponse(accessToken = "accessToken", refreshToken = "refreshToken")

        createTokenReturns(expectedBody)
        // Act
        val response = sut.register(user)
        // Assert
        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(expectedBody, response.body)
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

    private fun createTokenReturns(expectedBody: TokenResponse) {
        whenever(tokenService.createToken(anyOrNull()))
            .thenReturn(expectedBody)
    }

    private suspend fun emailExists() {
        whenever(repository.save(any()))
            .thenThrow(DataIntegrityViolationException(""))
    }

    // endregion
}
