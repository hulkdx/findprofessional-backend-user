package com.hulkdx.findprofessional

import com.hulkdx.findprofessional.base.IntegrationTest
import com.hulkdx.findprofessional.models.User
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.not
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.test.context.ActiveProfiles

@Suppress("FunctionName")
@OptIn(ExperimentalCoroutinesApi::class)
@SpringBootTest
@ActiveProfiles("test")
class AuthTokenServiceTest : IntegrationTest() {

    @Autowired
    private lateinit var sut: AuthTokenService

    @Autowired
    private lateinit var passwordEncoder: PasswordEncoder

    @BeforeEach
    fun setup() {
    }

    @Test
    fun `createToken result it not null`() {
        // Arrange
        val user = User("email", "password")
        // Act
        val result = sut.createToken(user)
        // Asserts
        assertThat(result, notNullValue())
    }

    @Test
    fun `createToken is not the same for different users`() {
        // Arrange
        val user1 = User("email1", "password1")
        val user2 = User("email2", "password2")
        // Act
        val result1 = sut.createToken(user1)
        val result2 = sut.createToken(user2)
        // Asserts
        assertThat(result1, not(result2))
    }

    @Test
    fun `createToken is not the same for same users`() = runTest {
        // Arrange
        val user = User("email", "password")
        // Act
        val result1 = sut.createToken(user)
        val result2 = sut.createToken(user)
        // Asserts
        assertNotEquals(result1, result2)

        val jwt1 = sut.decodeJwt(result1)
        val jwt2 = sut.decodeJwt(result2)
        assertJwtEquals(user, jwt1!!, jwt2!!)
    }

    private fun assertJwtEquals(user: User, jwt1: Jwt, jwt2: Jwt) {
        val sub1 = jwt1.claims["sub"].toString()
        val sub2 = jwt2.claims["sub"].toString()
        val subDec1 = passwordEncoder.matches(user.email, sub1)
        val subDec2 = passwordEncoder.matches(user.email, sub2)
        assertEquals(subDec1, subDec2)

        val claims1 = jwt1.claims.filter { it.key != "sub" }
        val claims2 = jwt2.claims.filter { it.key != "sub" }
        assertEquals(claims1, claims2)
        assertEquals(jwt1.headers, jwt2.headers)
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
}
