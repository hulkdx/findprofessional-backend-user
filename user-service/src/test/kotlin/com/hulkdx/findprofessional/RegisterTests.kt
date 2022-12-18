package com.hulkdx.findprofessional

import com.hulkdx.findprofessional.models.User
import com.hulkdx.findprofessional.utils.TestPasswordEncoder
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

    @Mock
    private lateinit var repository: UserRepository

    private val passwordEncoder: PasswordEncoder = TestPasswordEncoder()

    @BeforeEach
    fun setup() {

        sut = AuthController(
            repository,
            passwordEncoder,
        )
    }

    @Test
    fun `valid cases`() = runTest {
        // Arrange
        val email = "test@email.com"
        val password = "1234abdcx"
        val user = User(email, password)
        // Act
        val response = sut.register(user)
        // Assert
        assertEquals(HttpStatus.CREATED, response.statusCode)
        verify(repository).save(any())
    }

    @Test
    fun `when email exists then conflict`() = runTest {
        // Arrange
        val user = createUser()
        emailExists()
        // Act
        val response = sut.register(user)
        // Assert
        assertEquals(HttpStatus.CONFLICT, response.statusCode)
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
            val user = createUser(email = email)
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
            val user = createUser(password = password)
            // Act
            val response = sut.register(user)
            // Assert
            assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
        }
    }

    @Test
    fun `when id in request then bad request`() = runTest {
        // Arrange
        val id = 12
        val user = createUser(id = id)
        // Act
        val response = sut.register(user)
        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
    }

    @Test
    fun `don't store raw password`() = runTest {
        // Arrange
        val user = createUser()
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

    private fun createUser(
        email: String = "test@email.com",
        password: String = "1234abdcx",
        id: Int? = null,
    ) = User(
        email,
        password,
        id,
    )

    private suspend fun emailExists() {
        whenever(repository.save(any()))
            .thenThrow(DataIntegrityViolationException(""))
    }

    // endregion
}
