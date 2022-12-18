package com.hulkdx.findprofessional

import com.hulkdx.findprofessional.models.User
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.whenever
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.http.HttpStatus

@OptIn(ExperimentalCoroutinesApi::class)
@ExtendWith(MockitoExtension::class)
class RegisterTests {

    private lateinit var sut: AuthController

    @Mock
    private lateinit var repository: UserRepository

    @BeforeEach
    fun setup() {
        sut = AuthController(repository)
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
        assertThat(response.statusCode, `is`(HttpStatus.CREATED))
        verify(repository).save(user)
    }

    @Test
    fun `when email exists then conflict`() = runTest {
        // Arrange
        val user = createUser()
        emailExists(user)
        // Act
        val response = sut.register(user)
        // Assert
        assertThat(response.statusCode, `is`(HttpStatus.CONFLICT))
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
            assertThat(response.statusCode, `is`(HttpStatus.BAD_REQUEST))
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
            assertThat(response.statusCode, `is`(HttpStatus.BAD_REQUEST))
        }
    }

    // region helpers

    private suspend fun emailExists(user: User) {
        whenever(repository.save(user))
            .thenThrow(DataIntegrityViolationException(""))
    }

    private fun createUser(
        email: String = "test@email.com",
        password: String = "1234abdcx"
    ) = User(
        email,
        password,
    )

    // endregion
}
