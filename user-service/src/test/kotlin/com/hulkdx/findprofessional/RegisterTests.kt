package com.hulkdx.findprofessional

import com.hulkdx.findprofessional.controller.AuthController
import com.hulkdx.findprofessional.model.User
import com.hulkdx.findprofessional.model.response.AuthResponse
import com.hulkdx.findprofessional.model.response.TokenResponse
import com.hulkdx.findprofessional.model.response.UserResponse
import com.hulkdx.findprofessional.repository.UserRepository
import com.hulkdx.findprofessional.service.AuthService
import com.hulkdx.findprofessional.service.TokenService
import com.hulkdx.findprofessional.utils.TestPasswordEncoder
import com.hulkdx.findprofessional.utils.createRegisterRequest
import com.hulkdx.findprofessional.utils.errorMessage
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.condition.DisabledInNativeImage
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

@ExtendWith(MockitoExtension::class)
@DisabledInNativeImage
class RegisterTests {

    private lateinit var sut: AuthController

    @Mock
    private lateinit var repository: UserRepository

    @Mock
    private lateinit var tokenService: TokenService

    @BeforeEach
    fun setup() {
        val service = AuthService(repository, mock {}, TestPasswordEncoder())
        sut = AuthController(service, tokenService, mock {})
    }

    @Test
    fun `valid cases`() = runTest {
        // Arrange
        val email = "test@email.com"
        val password = "1234abdcx"
        val firstName = "Saba"
        val lastName = "Jaf"
        val profileImage = "some_url"
        val skypeId = "some skypeId"

        val request = createRegisterRequest(
            email = email,
            password = password,
            firstName = firstName,
            lastName = lastName,
            profileImage = profileImage,
            skypeId = skypeId,
        )
        val token = TokenResponse(accessToken = "accessToken", refreshToken = "refreshToken")
        createTokenReturns(token)
        // Act
        val response = sut.register(request)
        // Assert
        verify(repository).save(any())
        assertEquals(HttpStatus.OK, response.statusCode)

        val responseBody = response.body as AuthResponse
        val userResponse = responseBody.user as UserResponse
        assertEquals(token, responseBody.token)
        assertEquals(email, userResponse.email)
        assertEquals(firstName, userResponse.firstName)
        assertEquals(lastName, userResponse.lastName)
        assertEquals(profileImage, userResponse.profileImage)
        assertEquals(skypeId, userResponse.skypeId)
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
        createTokenSuccess()
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

    private suspend fun createTokenSuccess() {
        createTokenReturns(TokenResponse("", ""))
    }

    private suspend fun createTokenReturns(expectedBody: TokenResponse) {
        whenever(tokenService.createToken(anyOrNull()))
            .thenReturn(expectedBody)
        whenever(repository.save(any()))
            .thenAnswer { it.getArgument(0) }
    }

    private suspend fun emailExists() {
        whenever(repository.save(any()))
            .thenThrow(DataIntegrityViolationException(""))
    }

    // endregion
}
