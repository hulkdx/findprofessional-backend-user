package com.hulkdx.findprofessional

import com.hulkdx.findprofessional.models.RefreshRequest
import com.hulkdx.findprofessional.models.User
import com.hulkdx.findprofessional.utils.createJwt
import com.hulkdx.findprofessional.utils.createUser
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

    @Mock
    private lateinit var userRepository: UserRepository

    @BeforeEach
    fun setup() {
        val refreshService = RefreshService(
            tokenService,
            userRepository,
        )
        sut = AuthController(mock {}, mock {}, refreshService)
    }

    @Test
    fun `valid cases`() = runTest {
        // Arrange
        val authType = "Bearer"
        val accessToken = "accessToken"
        val refreshToken = "refreshToken"
        val userId = "1"
        val user: User = createUser(userId = userId.toInt())

        tokensAreValid(accessToken, refreshToken, userId)
        findUserByIdReturns(user, userId)
        whenever(tokenService.createToken(user))
            .thenReturn(mock {})
        // Act
        val response = sut.refresh("$authType $accessToken", RefreshRequest(refreshToken))
        // Assert
        assertEquals(HttpStatus.OK, response.statusCode)
    }

    @Test
    fun `when accessToken is invalid and refreshToken is valid then ok`() = runTest {
        // Arrange
        val authType = "Bearer"
        val accessToken = "accessToken"
        val refreshToken = "refreshToken"
        val userId = "1"
        val user: User = createUser(userId = userId.toInt())

        refreshToken(isValid = true, refreshToken, userId)
        accessToken(isValid = false, accessToken)
        findUserByIdReturns(user, userId)
        whenever(tokenService.createToken(user))
            .thenReturn(mock {})
        // Act
        val response = sut.refresh("$authType $accessToken", RefreshRequest(refreshToken))
        // Assert
        assertEquals(HttpStatus.OK, response.statusCode)
    }

    @Test
    fun `when authType is not Bearer then bad request`() = runTest {
        // Arrange
        val authType = "SomethingRandom"
        // Act
        val response = sut.refresh("$authType accessToken", RefreshRequest("refreshToken"))
        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
    }

    @Test
    fun `when auth is in an invalid format then bad request`() = runTest {
        // Arrange
        val auth = "INVALID_FORMAT_WITHOUT_SPACE"
        // Act
        val response = sut.refresh(auth, RefreshRequest("refreshToken"))
        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
    }

    @Test
    fun `when invalid refreshToken then unauthorized`() = runTest {
        // Arrange
        val refreshToken = "some_invalid_refreshToken"
        refreshToken(isValid = false, refreshToken)
        val accessToken = "accessToken"
        accessToken(isValid = true, accessToken)
        // Act
        val response = sut.refresh("Bearer $accessToken", RefreshRequest(refreshToken))
        // Assert
        assertEquals(HttpStatus.UNAUTHORIZED, response.statusCode)
    }

    @Test
    @MockitoSettings(strictness = Strictness.LENIENT)
    fun `when accessToken user id is different than refreshToken user id then unauthorized`() = runTest {
        // Arrange
        val refreshToken = "refreshToken"
        mockUserId(refreshToken, 1)
        val accessToken = "accessToken"
        mockUserId(accessToken, 2)
        // Act
        val response = sut.refresh("Bearer $accessToken", RefreshRequest(refreshToken))
        // Assert
        assertEquals(HttpStatus.UNAUTHORIZED, response.statusCode)
    }

    @Test
    fun `when tokens are valid but cannot find user in the database then unauthorized`() = runTest {
        // Arrange
        val accessToken = "accessToken"
        val refreshToken = "refreshToken"
        val userId = "1"
        tokensAreValid(accessToken, refreshToken, userId)
        findUserByIdReturns(null, userId)
        // Act
        val response = sut.refresh("Bearer $accessToken", RefreshRequest(refreshToken))
        // Assert
        assertEquals(HttpStatus.UNAUTHORIZED, response.statusCode)
    }

    // region helpers

    private suspend fun tokensAreValid(
        accessToken: String,
        refreshToken: String,
        userId: String
    ) {
        refreshToken(isValid = true, refreshToken, userId)
        accessToken(isValid = true, accessToken, userId)
    }

    private suspend fun refreshToken(
        isValid: Boolean,
        refreshToken: String,
        subject: String = "subject",
    ) {
        val jwt = createJwt(subject = subject)
        whenever(tokenService.decodeJwt(refreshToken))
            .thenReturn(jwt)
        whenever(tokenService.isTokenValid(jwt))
            .thenReturn(isValid)
    }

    private suspend fun accessToken(
        isValid: Boolean,
        accessToken: String,
        subject: String = "subject",
    ) {
        val jwt = if (isValid) createJwt(subject = subject) else null
        whenever(tokenService.decodeJwt(accessToken))
            .thenReturn(jwt)
    }

    private suspend fun mockUserId(token: String, userId: Int) {
        val jwt = createJwt(subject = userId.toString())

        whenever(tokenService.decodeJwt(token))
            .thenReturn(jwt)
        whenever(tokenService.isTokenValid(jwt))
            .thenReturn(true)
    }

    private suspend fun findUserByIdReturns(user: User?, userId: String) {
        whenever(userRepository.findById(userId.toInt()))
            .thenReturn(user)
    }

    // endregion
}
