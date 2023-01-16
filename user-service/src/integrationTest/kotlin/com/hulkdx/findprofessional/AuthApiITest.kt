package com.hulkdx.findprofessional

import com.hulkdx.findprofessional.base.IntegrationTest
import com.hulkdx.findprofessional.models.AuthRequest
import com.hulkdx.findprofessional.models.TokenResponse
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient
import com.hulkdx.findprofessional.utils.body
import com.hulkdx.findprofessional.utils.response
import org.junit.jupiter.api.BeforeEach
import org.mockito.kotlin.whenever
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.HttpHeaders
import java.time.Clock
import java.time.Instant
import java.time.temporal.ChronoUnit


@Suppress("FunctionName")
@AutoConfigureWebTestClient
class AuthApiITest : IntegrationTest() {

    @Autowired
    private lateinit var client: WebTestClient

    @MockBean
    private lateinit var clock: Clock

    @BeforeEach
    internal fun setUp() {
        whenever(clock.instant()).thenReturn(Instant.now())
    }

    @Test
    fun `register then can login`() {
        val body = AuthRequest("test@gmail.com", "123AsdzxcvB!!")
        register(body)
        login(body)
    }

    @Test
    fun `refreshToken test cases`() {
        val body = AuthRequest("test@gmail.com", "123AsdzxcvB!!")

        register(body)
        val token = login(body)
        refresh(token)
            .isOk
        timePassed(31, ChronoUnit.DAYS)
        refresh(token)
            .isUnauthorized
    }

    private fun refresh(response: TokenResponse) =
        client.post()
            .uri("/auth/refresh")
            .header(HttpHeaders.AUTHORIZATION, "Bearer ${response.accessToken}")
            .contentType(MediaType.APPLICATION_JSON)
            .body(response.refreshToken)


    private fun login(body: AuthRequest): TokenResponse {
        return client.post()
            .uri("/auth/login")
            .body(body)
            .isOk
            .response(TokenResponse::class.java)
    }

    private fun register(body: AuthRequest) {
        client.post()
            .uri("/auth/register")
            .body(body)
            .isCreated
    }

    private fun timePassed(time: Long, unit: ChronoUnit) {
        whenever(clock.instant()).thenReturn(Instant.now().plus(time, unit))
    }
}
