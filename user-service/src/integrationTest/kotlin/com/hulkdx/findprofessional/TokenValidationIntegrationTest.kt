package com.hulkdx.findprofessional

import com.hulkdx.findprofessional.base.IntegrationTest
import com.hulkdx.findprofessional.service.TokenService
import com.hulkdx.findprofessional.utils.createUser
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mock
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.oauth2.jwt.JwtEncoder
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder
import java.time.Clock
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.time.temporal.ChronoUnit.DAYS
import java.time.temporal.ChronoUnit.HOURS

@OptIn(ExperimentalCoroutinesApi::class)
class TokenValidationIntegrationTest : IntegrationTest() {

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
    fun `accessToken should not be valid after time passed passed`() = runTest {
        // Arrange
        val time = (1L to HOURS)
        val user = createUser(email = "email", password = "password")
        // Act
        val token = sut.createToken(user).accessToken
        // Asserts
        timePassed(time.first, time.second)
        val isValid = sut.isTokenValid(token)
        assertEquals(false, isValid)
    }

    @Test
    fun `refreshToken should not be valid after 30 days passed`() = runTest {
        // Arrange
        val user = createUser(email = "email", password = "password")
        // Act
        val token = sut.createToken(user).refreshToken
        // Asserts
        timePassed(30, DAYS)
        val isValid = sut.isTokenValid(token)
        assertEquals(false, isValid)
    }

    // region helpers

    private fun timePassed(time: Long, unit: ChronoUnit) {
        whenever(clock.instant()).thenReturn(Instant.now().plus(time, unit))
    }

    // endregion helpers
}
