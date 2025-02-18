package com.hulkdx.findprofessional

import com.hulkdx.findprofessional.controller.AuthController
import com.hulkdx.findprofessional.model.response.UserResponse
import com.hulkdx.findprofessional.repository.UserRepository
import com.hulkdx.findprofessional.service.AuthService
import com.hulkdx.findprofessional.service.TokenService
import com.hulkdx.findprofessional.service.UserService
import com.hulkdx.findprofessional.utils.createJwt
import com.hulkdx.findprofessional.utils.createPro
import com.hulkdx.findprofessional.utils.createUser
import com.hulkdx.findprofessional.utils.createUserUpdateRequest
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.condition.DisabledInNativeImage
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import org.springframework.http.HttpStatus
import org.springframework.security.oauth2.jwt.Jwt

@ExtendWith(MockitoExtension::class)
@DisabledInNativeImage
class UpdateUserTests {

    private lateinit var sut: AuthController

    @Mock
    private lateinit var userRepository: UserRepository

    @Mock
    private lateinit var tokenService: TokenService

    @BeforeEach
    fun setup() {
        val userService = UserService(userRepository)
        sut = AuthController(
            authService = AuthService(mock {}, mock {}, mock {}),
            tokenService = tokenService,
            userService = userService,
            refreshService = mock {},
        )
    }

    @Test
    fun `update user when it found a user`() = runTest {
        // Arrange
        val user = createUser()
        val accessToken = "accessToken"
        val token = "Bearer $accessToken"
        val request = createUserUpdateRequest(
            firstName = "newFirstName",
            lastName = "newLastName",
            profileImage = "newProfileImage",
            skypeId = "newSkypeId"
        )
        val jwtMock = mock<Jwt> {}
        whenever(tokenService.decodeJwt(accessToken)).thenReturn(jwtMock)
        whenever(tokenService.isTokenValid(jwtMock)).thenReturn(true)
        whenever(jwtMock.subject).thenReturn(user.id.toString())
        whenever(userRepository.findById(user.id!!)).thenReturn(user)
        // Act
        val response = sut.updateUser(token, request)
        // Assert
        assertThat(response.statusCode, `is`(HttpStatus.OK))
        val responseBody = response.body as UserResponse
        assertThat(responseBody.firstName, `is`("newFirstName"))
        assertThat(responseBody.lastName, `is`("newLastName"))
        assertThat(responseBody.profileImage, `is`("newProfileImage"))
        assertThat(responseBody.skypeId, `is`("newSkypeId"))
    }

    @Test
    fun `if accessToken is invalid format should fail with bad request`() = runTest {
        // Arrange
        val token = "BADFORMTAaccessToken"
        // Act
        val response = sut.updateUser(token, createUserUpdateRequest())
        // Assert
        assertThat(response.statusCode, `is`(HttpStatus.BAD_REQUEST))
    }

    @Test
    fun `if accessToken is not valid then it should fail with bad request`() = runTest {
        // Arrange
        val accessToken = "accessToken"
        val token = "Bearer $accessToken"
        accessToken(isValid = false, accessToken)
        // Act
        val response = sut.updateUser(token, createUserUpdateRequest())
        // Assert
        assertThat(response.statusCode, `is`(HttpStatus.BAD_REQUEST))
    }

    @Test
    fun `update user when user is a pro-user returns forbidden`() = runTest {
        // Arrange
        val user = createPro(id = 5)
        val accessToken = "accessToken"
        val token = "Bearer $accessToken"
        val request = createUserUpdateRequest(
            firstName = "newFirstName",
            lastName = "newLastName",
            profileImage = "newProfileImage",
            skypeId = "newSkypeId"
        )
        val jwtMock = mock<Jwt> {}
        whenever(tokenService.decodeJwt(accessToken)).thenReturn(jwtMock)
        whenever(tokenService.isTokenValid(jwtMock)).thenReturn(true)
        whenever(jwtMock.subject).thenReturn(user.id.toString())
        whenever(userRepository.findById(user.id!!)).thenReturn(null)
        // Act
        val response = sut.updateUser(token, request)
        // Assert
        assertThat(response.statusCode, `is`(HttpStatus.FORBIDDEN))
    }

    private suspend fun accessToken(isValid: Boolean, accessToken: String, userId: String = "1") {
        val jwt = createJwt(subject = userId)
        whenever(tokenService.decodeJwt(accessToken)).thenReturn(jwt)
        whenever(tokenService.isTokenValid(jwt)).thenReturn(isValid)
    }
}
