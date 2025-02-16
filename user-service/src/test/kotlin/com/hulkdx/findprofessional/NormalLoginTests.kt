@file:Suppress("UnnecessaryVariable")

package com.hulkdx.findprofessional

import com.hulkdx.findprofessional.controller.AuthController
import com.hulkdx.findprofessional.model.response.AuthResponse
import com.hulkdx.findprofessional.model.request.LoginRequest
import com.hulkdx.findprofessional.model.User
import com.hulkdx.findprofessional.model.response.UserResponse
import com.hulkdx.findprofessional.model.response.TokenResponse
import com.hulkdx.findprofessional.repository.UserRepository
import com.hulkdx.findprofessional.service.AuthService
import com.hulkdx.findprofessional.service.TokenService
import com.hulkdx.findprofessional.utils.TestPasswordEncoder
import com.hulkdx.findprofessional.utils.createUser
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.condition.DisabledInNativeImage
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.anyOrNull
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import org.springframework.http.HttpStatus
import org.springframework.security.crypto.password.PasswordEncoder

@ExtendWith(MockitoExtension::class)
@DisabledInNativeImage
class NormalLoginTests {

    private lateinit var sut: AuthController

    @Mock
    private lateinit var repository: UserRepository
    @Mock
    private lateinit var tokenService: TokenService

    private val passwordEncoder: PasswordEncoder = TestPasswordEncoder()

    private lateinit var service: AuthService

    @BeforeEach
    fun setup() {
        service = AuthService(repository, mock{}, passwordEncoder)
        sut = AuthController(service, tokenService, mock {}, mock {})
    }

    @Test
    fun `valid password then ok`() = runTest {
        // Arrange
        val requestEmail = "test@email.com"
        val requestPassword = "1234abdcx"
        val request = LoginRequest(requestEmail, requestPassword)
        val token = TokenResponse(accessToken = "accessToken", refreshToken = "refreshToken")
        val skypeId = "some skype id"

        findByEmailReturnsValidUser(requestEmail, requestPassword, skypeId)
        createTokenReturns(token)
        // Act
        val response = sut.login(request)
        // Assert
        verify(repository).findByEmail(requestEmail)
        assertEquals(HttpStatus.OK, response.statusCode)

        val responseBody = (response.body as AuthResponse)
        val userResponse = responseBody.user as UserResponse
        assertEquals(token, responseBody.token)
        assertEquals(requestEmail, userResponse.email)
        assertEquals(skypeId, userResponse.skypeId)
    }

    @Test
    fun `invalid password then unauthorized`() = runTest {
        // Arrange
        val requestEmail = "test@email.com"
        val requestPassword = "1234abdcx"
        val dbEmail = "test@email.com"
        val dbPassword = "some_invalid_password"

        val user = createUser(email = dbEmail, password = dbPassword)
        val request = LoginRequest(requestEmail, requestPassword)
        whenever(repository.findByEmail(requestEmail)).thenReturn(user)
        // Act
        val response = sut.login(request)
        // Assert
        verify(repository).findByEmail(requestEmail)
        assertEquals(HttpStatus.UNAUTHORIZED, response.statusCode)
    }

    @Test
    fun `when user does not exists then unauthorized`() = runTest {
        // Arrange
        val email = "test@email.com"
        val password = "1234abdcx"
        val request = LoginRequest(email, password)
        // Act
        val response = sut.login(request)
        // Assert
        verify(repository).findByEmail(email)
        assertEquals(HttpStatus.UNAUTHORIZED, response.statusCode)
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
            val user = LoginRequest(email = email, password = "1234abdcx")
            // Act
            val response = sut.login(user)
            // Assert
            assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
        }
    }
    // region helpers

    private fun createTokenReturns(expectedBody: TokenResponse) {
        whenever(tokenService.createToken(anyOrNull()))
            .thenReturn(expectedBody)
    }

    private suspend fun findByEmailReturnsValidUser(
        requestEmail: String,
        requestPassword: String,
        skypeId: String,
    ): User {
        val dbEmail = requestEmail
        val dbPassword = passwordEncoder.encode(requestPassword)
        val user = createUser(email = dbEmail, password = dbPassword, skypeId = skypeId)
        whenever(repository.findByEmail(requestEmail))
            .thenReturn(user)
        return user
    }

    // end region
}
