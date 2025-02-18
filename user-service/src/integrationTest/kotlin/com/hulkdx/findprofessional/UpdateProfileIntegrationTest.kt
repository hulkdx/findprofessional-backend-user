package com.hulkdx.findprofessional

import com.hulkdx.findprofessional.base.IntegrationTest
import com.hulkdx.findprofessional.model.response.UserResponse
import com.hulkdx.findprofessional.repository.UserRepository
import com.hulkdx.findprofessional.service.TokenService
import com.hulkdx.findprofessional.utils.body
import com.hulkdx.findprofessional.utils.createUser
import com.hulkdx.findprofessional.utils.createUserUpdateRequest
import com.hulkdx.findprofessional.utils.response
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.http.HttpHeaders
import org.springframework.test.web.reactive.server.WebTestClient

@AutoConfigureWebTestClient
class UpdateProfileIntegrationTest : IntegrationTest() {

    @Autowired
    private lateinit var client: WebTestClient

    @Autowired
    private lateinit var tokenService: TokenService

    @Autowired
    private lateinit var userRepository: UserRepository

    @BeforeEach
    fun setUp() {
    }

    @AfterEach
    fun tearDown() = runTest {
        userRepository.deleteAll()
    }

    @Test
    fun `update profile`() = runTest {
        // Arrange
        val user = userRepository.save(
            createUser(
                id = null,
                firstName = "test first name",
            )
        )
        val body = createUserUpdateRequest(
            firstName = "updated first name",
            lastName = "updated last name",
            profileImage = "updated profile image",
            skypeId = "updated skype id",
        )
        val accessToken = tokenService.createAccessToken(user)
        // Act
        val response = client.post()
            .uri("/auth/user")
            .header(HttpHeaders.AUTHORIZATION, "Bearer $accessToken")
            .body(body)
            .isOk
            .response(UserResponse::class.java)
        // Asserts
        assertEquals(response.firstName, "updated first name")
        assertEquals(response.lastName, "updated last name")
        assertEquals(response.profileImage, "updated profile image")
        assertEquals(response.skypeId, "updated skype id")
    }

}
