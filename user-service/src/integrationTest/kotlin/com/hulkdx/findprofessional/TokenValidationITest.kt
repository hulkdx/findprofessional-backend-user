package com.hulkdx.findprofessional

import com.hulkdx.findprofessional.base.IntegrationTest
import com.hulkdx.findprofessional.models.User
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mock
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.oauth2.jwt.JwtEncoder
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder
import org.springframework.test.context.ActiveProfiles
import java.time.Clock
import java.time.Instant

@Suppress("FunctionName")
@OptIn(ExperimentalCoroutinesApi::class)
@SpringBootTest
@ActiveProfiles("test")
class TokenValidationITest : IntegrationTest() {

    companion object {
        private lateinit var jwtEncoder: JwtEncoder
        private lateinit var jwtDecoder: ReactiveJwtDecoder

        @Suppress("unused")
        @BeforeAll
        @JvmStatic
        fun setup(
            @Autowired jwtEncoder: JwtEncoder,
            @Autowired jwtDecoder: ReactiveJwtDecoder,
        ) {
            this.jwtEncoder = jwtEncoder
            this.jwtDecoder = jwtDecoder
        }
    }

    private lateinit var sut: TokenService

    @Mock
    private lateinit var clock: Clock

    @BeforeEach
    fun setup() {
        whenever(clock.instant()).thenReturn(Instant.now())

        sut = TokenService(
            jwtEncoder,
            jwtDecoder,
            clock,
        )
    }

    @Test
    fun `accessToken should not be valid after 10 minutes passed`() = runTest {
        // Arrange
        val user = User("email", "password")
        // Act
        val token = sut.createToken(user).accessToken
        // Asserts
        `10minutesPassed`()
        val isValid = sut.isTokenValid(token)
        assertEquals(false, isValid)
    }

    // region helpers

    private fun `10minutesPassed`() {
        whenever(clock.instant()).thenReturn(Instant.now().plusSeconds(10 * 60))
    }

    // endregion helpers
}
