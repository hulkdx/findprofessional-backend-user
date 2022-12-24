package com.hulkdx.findprofessional


import com.hulkdx.findprofessional.models.RegisterRequest
import com.hulkdx.findprofessional.utils.TestPasswordEncoder
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.http.HttpStatus
import org.springframework.security.crypto.password.PasswordEncoder

@OptIn(ExperimentalCoroutinesApi::class)
@ExtendWith(MockitoExtension::class)
class LoginTests {

    private lateinit var sut: AuthController

    @Mock
    private lateinit var repository: UserRepository

    private val passwordEncoder: PasswordEncoder = TestPasswordEncoder()

    private lateinit var service: AuthService

    @BeforeEach
    fun setup() {
        service = AuthService(repository, passwordEncoder)
        sut = AuthController(service)
    }

    @Test
    fun `when user does not exists then conflict`() = runTest {
        // Arrange
        val email = "test@email.com"
        val password = "1234abdcx"
        val request = RegisterRequest(email, password)
        // Act
        val response = sut.login(request)
        // Assert
        verify(repository).findByEmail(email)
        assertEquals(HttpStatus.CONFLICT, response.statusCode)
    }
}
