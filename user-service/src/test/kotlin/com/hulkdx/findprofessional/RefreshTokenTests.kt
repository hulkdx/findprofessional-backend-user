package com.hulkdx.findprofessional

import com.hulkdx.findprofessional.utils.createJwt
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.junit.jupiter.MockitoSettings
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import org.mockito.quality.Strictness
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
    @MockitoSettings(strictness = Strictness.LENIENT)
    fun `when invalid accessToken then unauthorized`() = runTest {
        // Arrange
        val accessToken = "some_invalid_accessToken"
        isTokenValid(accessToken, false)
        // Act
        val response = sut.refresh("Bearer $accessToken", "refreshToken")
        // Assert
        assertEquals(HttpStatus.UNAUTHORIZED, response.statusCode)
    }

    @Test
    fun `when invalid refreshToken then unauthorized`() = runTest {
        // Arrange
        val refreshToken = "some_invalid_refreshToken"
        isTokenValid(refreshToken, false)
        val accessToken = "accessToken"
        isTokenValid(accessToken, true)
        // Act
        val response = sut.refresh("Bearer $accessToken", refreshToken)
        // Assert
        assertEquals(HttpStatus.UNAUTHORIZED, response.statusCode)
    }

    @Test
    fun `when accessToken user id is different than refreshToken user id then unauthorized`() = runTest {
        // Arrange
        val refreshToken = "refreshToken"
        setUserId(refreshToken, 1)
        val accessToken = "accessToken"
        setUserId(accessToken, 2)
        // Act
        val response = sut.refresh("Bearer $accessToken", refreshToken)
        // Assert
        assertEquals(HttpStatus.UNAUTHORIZED, response.statusCode)
    }

    // region helpers

    private suspend fun isTokenValid(token: String, isValid: Boolean) {
        val jwt = createJwt(subject = "subject")
        whenever(tokenService.decodeJwt(token))
            .thenReturn(jwt)
        whenever(tokenService.isTokenValid(jwt))
            .thenReturn(isValid)
    }

    private suspend fun setUserId(token: String, userId: Int) {
        val jwt = createJwt(subject = userId.toString())

        whenever(tokenService.decodeJwt(token))
            .thenReturn(jwt)
        whenever(tokenService.isTokenValid(jwt))
            .thenReturn(true)
    }

    // endregion
}
