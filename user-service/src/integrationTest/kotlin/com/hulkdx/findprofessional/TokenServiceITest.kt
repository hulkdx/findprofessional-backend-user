package com.hulkdx.findprofessional

import com.hulkdx.findprofessional.base.IntegrationTest
import com.hulkdx.findprofessional.models.User
import com.nimbusds.jose.jwk.JWKSet
import com.nimbusds.jose.jwk.RSAKey
import com.nimbusds.jose.jwk.gen.RSAKeyGenerator
import com.nimbusds.jose.jwk.source.ImmutableJWKSet
import com.nimbusds.jose.proc.SecurityContext
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.not
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mock
import org.mockito.Mockito.mockStatic
import org.mockito.Mockito.spy
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.jwt.JwtEncoder
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder
import org.springframework.test.context.ActiveProfiles
import java.time.Clock
import java.time.Instant


@OptIn(ExperimentalCoroutinesApi::class)
class TokenServiceITest : IntegrationTest() {

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
    fun `createToken result it not null`() {
        // Arrange
        val user = User("email", "password")
        // Act
        val result = sut.createAccessToken(user)
        // Asserts
        assertThat(result, notNullValue())
    }

    @Test
    fun `createAccessToken decode tests`() = runTest {
        // Arrange
        val userId = 123
        val user = User("email", "password", id = userId)
        // Act
        val token = sut.createAccessToken(user)
        // Asserts
        val decoded = sut.decodeJwt(token)
        assertNotNull(decoded)
        val issuer = decoded?.claims?.get("iss") ?: ""
        assertEquals("com.hulkdx.findprofessional", issuer)
        val subject = decoded?.claims?.get("sub") ?: ""
        assertEquals(userId.toString(), subject)
    }

    @Test
    fun `createToken is not the same for different users`() {
        // Arrange
        val user1 = User("email1", "password1", id = 1)
        val user2 = User("email2", "password2", id = 2)
        // Act
        val result1 = sut.createAccessToken(user1)
        val result2 = sut.createAccessToken(user2)
        // Asserts
        assertThat(result1, not(result2))
    }

    @Test
    fun `createToken is not the same for same users`() = runTest {
        // Arrange
        val user = User("email", "password")
        // Act
        val result1 = sut.createAccessToken(user)
        whenever(clock.instant()).thenReturn(Instant.now().plusSeconds(1000))
        val result2 = sut.createAccessToken(user)
        // Asserts
        assertNotEquals(result1, result2)

        val jwt1 = sut.decodeJwt(result1)
        val jwt2 = sut.decodeJwt(result2)
        assertJwtEquals(jwt1!!, jwt2!!)
    }

    @Test
    fun `isTokenValid with invalid tokens`() = runTest {
        // Arrange
        val token = ""
        // Act
        val result = sut.isTokenValid(token)
        // Asserts
        assertThat(result, `is`(false))
    }

    @Test
    fun `is not valid with a new decoder and encoder`() = runTest {
        // Arrange
        val user = User("email", "password")
        val originalSut = createOriginal()
        val newSut = createNew()
        // Act
        val token = newSut.createAccessToken(user)
        val isValid = originalSut.isTokenValid(token)
        // Asserts
        assertEquals(false, isValid)
    }

    @Test
    fun `refreshToken decode tests`() = runTest {
        // Arrange
        val userId = 123
        val user = User("email", "password", id = userId)
        // Act
        val token = sut.createRefreshToken(user)
        // Asserts
        val decoded = sut.decodeJwt(token)
        assertNotNull(decoded)
        val subject = decoded?.claims?.get("sub") ?: ""
        assertEquals(userId.toString(), subject)
    }

    // region helpers

    private fun assertJwtEquals(jwt1: Jwt, jwt2: Jwt) {
        val claims1 = jwt1.claims.filter { it.key != "iat" }.filter { it.key != "exp" }
        val claims2 = jwt2.claims.filter { it.key != "iat" }.filter { it.key != "exp" }
        assertEquals(claims1, claims2)
        assertEquals(jwt1.headers, jwt2.headers)
    }

    private fun createNew(): TokenService {
        val rsaKey = RSAKeyGenerator(2048).generate()
        val privateKey = rsaKey.toRSAPrivateKey()
        val publicKey = rsaKey.toRSAPublicKey()
        val jwk: RSAKey = RSAKey.Builder(publicKey).privateKey(privateKey).build()
        val jwks = ImmutableJWKSet<SecurityContext>(JWKSet(jwk))
        val newEncoder = NimbusJwtEncoder(jwks)
        val newDecoder = NimbusReactiveJwtDecoder.withPublicKey(publicKey).build()
        return TokenService(
            newEncoder,
            newDecoder,
            clock,
        )
    }

    private fun createOriginal() = TokenService(
        jwtEncoder,
        jwtDecoder,
        clock,
    )

    // endregion helpers
}
