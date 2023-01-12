package com.hulkdx.findprofessional

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import org.springframework.http.HttpStatus

@OptIn(ExperimentalCoroutinesApi::class)
@ExtendWith(MockitoExtension::class)
class RefreshTokenTests {

    private lateinit var sut: AuthController

    @Mock
    private lateinit var tokenService: TokenService

    @BeforeEach
    fun setup() {
        val refreshService = RefreshService(
            tokenService,
        )
        sut = AuthController(mock {}, mock {}, refreshService)
    }

    @Test
    fun `when authType is not Bearer then bad request`() = runTest {
        // Arrange
        val authType = "SomethingRandom"
        // Act
        val response = sut.refresh("$authType accessToken", "refreshToken")
        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
    }

    @Test
    fun `when auth is in an invalid format then bad request`() = runTest {
        // Arrange
        val auth = "INVALID_FORMAT_WITHOUT_SPACE"
        // Act
        val response = sut.refresh(auth, "refreshToken")
        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
    }

    @Test
    fun `when invalid accessToken then unauthorized`() = runTest {
        // Arrange
        val accessToken = "some_invalid_accessToken"
        whenever(tokenService.isTokenValid(accessToken)).thenReturn(false)
        // Act
        val response = sut.refresh("Bearer $accessToken", "refreshToken")
        // Assert
        assertEquals(HttpStatus.UNAUTHORIZED, response.statusCode)
    }

    // region helpers

    // endregion
}
