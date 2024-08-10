@file:Suppress("FunctionName", "SameParameterValue")

package com.hulkdx.findprofessional

import com.hulkdx.findprofessional.base.IntegrationTest
import com.hulkdx.findprofessional.model.RegisterRequest
import com.hulkdx.findprofessional.model.AuthResponse
import com.hulkdx.findprofessional.model.RefreshRequest
import com.hulkdx.findprofessional.model.TokenResponse
import com.hulkdx.findprofessional.utils.body
import com.hulkdx.findprofessional.utils.createRegisterRequest
import com.hulkdx.findprofessional.utils.response
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient
import java.time.Clock
import java.time.Instant
import java.time.temporal.ChronoUnit

@AutoConfigureWebTestClient
@OptIn(ExperimentalCoroutinesApi::class)
class AuthIntegrationTest : IntegrationTest() {

    @Autowired
    private lateinit var client: WebTestClient

    @Autowired
    private lateinit var userRepository: UserRepository

    @MockBean
    private lateinit var clock: Clock

    @BeforeEach
    fun setUp() {
        whenever(clock.instant()).thenReturn(Instant.now())
    }

    @AfterEach
    fun tearDown() = runTest {
        userRepository.deleteAll()
    }

    @Test
    fun `register then can login`() {
        val body = createRegisterRequest("test@gmail.com", "123AsdzxcvB!!")
        register(body)
        login(body)
    }

    @Test
    fun `refreshToken test cases`() {
        val response = loginWithRandomUser()

        refresh(response.token)
            .isOk

        timePassed(31, ChronoUnit.DAYS)
        refresh(response.token)
            .isUnauthorized
    }

    private fun refresh(response: TokenResponse) =
        client.post()
            .uri("/auth/refresh")
            .header(HttpHeaders.AUTHORIZATION, "Bearer ${response.accessToken}")
            .contentType(MediaType.APPLICATION_JSON)
            .body(RefreshRequest(response.refreshToken))


    private fun login(body: RegisterRequest): AuthResponse {
        return client.post()
            .uri("/auth/login")
            .body(body)
            .isOk
            .response(AuthResponse::class.java)
    }

    private fun register(body: RegisterRequest) {
        client.post()
            .uri("/auth/register")
            .body(body)
            .isOk
    }

    private fun timePassed(time: Long, unit: ChronoUnit) {
        whenever(clock.instant()).thenReturn(Instant.now().plus(time, unit))
    }

    private fun loginWithRandomUser(): AuthResponse {
        val body = createRegisterRequest("test@gmail.com", "123AsdzxcvB!!")
        register(body)
        return login(body)
    }
}
