package com.hulkdx.findprofessional

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.*
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.junit.jupiter.MockitoExtension

@OptIn(ExperimentalCoroutinesApi::class)
@ExtendWith(MockitoExtension::class)
class AuthControllerTest {

    private lateinit var sut: AuthController

    @Mock
    private lateinit var repository: UserRepository

    @BeforeEach
    fun setup() {
        sut = AuthController(repository)
    }

    @Test
    fun `register valid cases`() = runTest {
        // Arrange
        val email = "test@email.com"
        val password = "1234"
        val user = User(email, password)
        // Act
        sut.register(user)
        // Assert
        verify(repository).save(user)
    }
}
