package com.hulkdx.findprofessional

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.reactor.mono
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.jwt.JwtEncoder
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder
import java.time.Clock
import java.time.Instant

@OptIn(ExperimentalCoroutinesApi::class)
@ExtendWith(MockitoExtension::class)
class TokenServiceTest {

    private lateinit var sut: TokenService

    @Mock
    private lateinit var passwordEncoder: PasswordEncoder

    @Mock
    private lateinit var jwtEncoder: JwtEncoder

    @Mock
    private lateinit var jwtDecoder: ReactiveJwtDecoder

    @Mock
    private lateinit var clock: Clock

    @BeforeEach
    fun setup() {
        sut = TokenService(
            passwordEncoder,
            jwtEncoder,
            jwtDecoder,
            clock,
        )
    }

    @Test
    fun `isTokenValid when null mono then return false`() = runTest {
        // Arrange
        jwtDecoderReturns(null)
        // Act
        val result = sut.isTokenValid("token")
        // Asserts
        assertThat(result, `is`(false))
    }

    @Test
    fun `isTokenValid when null expiredAt then return false`() = runTest {
        // Arrange
        jwtDecoderReturns(createJwt(expiredAt = null))
        // Act
        val result = sut.isTokenValid("token")
        // Asserts
        assertThat(result, `is`(false))
    }

    // region helpers

    private fun jwtDecoderReturns(jwt: Jwt?) {
        whenever(jwtDecoder.decode(any()))
            .thenReturn(mono { jwt })
    }

    private fun createJwt(expiredAt: Instant?) = Jwt(
        "123",
        Instant.now(),
        expiredAt,
        mapOf("" to ""),
        mapOf("" to "")
    )

    // endregion helpers
}
