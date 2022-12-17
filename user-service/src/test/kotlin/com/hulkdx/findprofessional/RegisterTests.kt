package com.hulkdx.findprofessional

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.*
import org.junit.jupiter.api.*
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import org.mockito.junit.jupiter.MockitoExtension
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
        val password = "1234"
        val user = User(email, password)
        // Act
        val response = sut.register(user)
        // Assert
        verify(repository).save(user)
        assertThat(response.statusCode, `is`(HttpStatus.CREATED))
    }

    @Test
    fun `when email exists then conflict`() = runTest {
        // Arrange
        val user = User("__irrelevent__@email.com", "__irrelevent__")
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
            val user = User(email, "__irrelevant__")
            // Act
            val response = sut.register(user)
            // Assert
            assertThat(response.statusCode, `is`(HttpStatus.BAD_REQUEST))
        }
    }

    // region helpers

    private suspend fun emailExists(user: User) {
        `when`(repository.save(user)).thenThrow(DataIntegrityViolationException(""))
    }

    // endregion
}
