package com.hulkdx.findprofessional

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.reactor.mono
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.MatcherAssert.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.oauth2.jwt.JwtEncoder
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder

@OptIn(ExperimentalCoroutinesApi::class)
@ExtendWith(MockitoExtension::class)
class AuthTokenServiceTest {

    private lateinit var sut: AuthTokenService

    @Mock
    private lateinit var passwordEncoder: PasswordEncoder

    @Mock
    private lateinit var jwtEncoder: JwtEncoder

    @Mock
    private lateinit var jwtDecoder: ReactiveJwtDecoder

    @BeforeEach
    fun setup() {
        sut = AuthTokenService(
            passwordEncoder,
            jwtEncoder,
            jwtDecoder,
        )
    }

    @Test
    fun `test createToken`() = runTest {
        // Arrange
        whenever(jwtDecoder.decode(any()))
            .thenReturn(mono { null })
        // Act
        val result = sut.isTokenValid("token")
        // Asserts
        assertThat(result, `is`(false))
    }
}
