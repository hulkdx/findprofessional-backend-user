package com.hulkdx.findprofessional

import com.hulkdx.findprofessional.base.IntegrationTest
import com.hulkdx.findprofessional.models.AuthRequest
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient
import reactor.core.publisher.Mono
import com.hulkdx.findprofessional.utils.body


@AutoConfigureWebTestClient
class RefreshTokenITest : IntegrationTest() {

    @Autowired
    private lateinit var client: WebTestClient

    @Test
    fun `register then can login`() {
        val body = AuthRequest("test@gmail.com", "123AsdzxcvB!!")
        client.post()
            .uri("/auth/register")
            .body(body)
            .isCreated
        client.post()
            .uri("/auth/login")
            .body(body)
            .isOk
    }
}
