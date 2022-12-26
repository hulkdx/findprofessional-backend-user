package com.hulkdx.findprofessional


import com.hulkdx.findprofessional.models.RegisterRequest
import com.hulkdx.findprofessional.models.User
import com.hulkdx.findprofessional.utils.TestPasswordEncoder
import com.hulkdx.findprofessional.utils.createRegisterRequest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.anyOrNull
import org.mockito.kotlin.whenever
import org.springframework.http.HttpStatus
import org.springframework.security.crypto.password.PasswordEncoder

@OptIn(ExperimentalCoroutinesApi::class)
@ExtendWith(MockitoExtension::class)
class LoginTests {

    private lateinit var sut: AuthController

    @Mock
    private lateinit var repository: UserRepository

    @Mock
    private lateinit var tokenService: TokenService

    private val passwordEncoder: PasswordEncoder = TestPasswordEncoder()

    private lateinit var service: AuthService

    @BeforeEach
    fun setup() {
        service = AuthService(repository, passwordEncoder)
        sut = AuthController(service, tokenService)
    }

    @Test
    fun `valid password then ok`() = runTest {
        // Arrange
        val requestEmail = "test@email.com"
        val requestPassword = "1234abdcx"
        val dbEmail = "test@email.com"
        val dbPassword = passwordEncoder.encode(requestPassword)
        val expectedBody = "Some JWT token"

        val user = User(dbEmail, dbPassword)
        val request = RegisterRequest(requestEmail, requestPassword)
        whenever(repository.findByEmail(requestEmail)).thenReturn(user)
        whenever(tokenService.createToken(anyOrNull())).thenReturn(expectedBody)
        // Act
        val response = sut.login(request)
        // Assert
        verify(repository).findByEmail(requestEmail)
        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(expectedBody, response.body)
    }

    @Test
    fun `invalid password then unauthorized`() = runTest {
        // Arrange
        val requestEmail = "test@email.com"
        val requestPassword = "1234abdcx"
        val dbEmail = "test@email.com"
        val dbPassword = "some_invalid_password"

        val user = User(dbEmail, dbPassword)
        val request = RegisterRequest(requestEmail, requestPassword)
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
        val request = RegisterRequest(email, password)
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
            val user = createRegisterRequest(email = email)
            // Act
            val response = sut.login(user)
            // Assert
            assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
        }
    }
}
