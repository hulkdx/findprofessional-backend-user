package com.hulkdx.findprofessional
import com.hulkdx.findprofessional.base.IntegrationTest
import com.hulkdx.findprofessional.models.User
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.oauth2.jwt.JwtEncoder
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder
import org.springframework.test.context.ActiveProfiles

@Suppress("FunctionName")
@OptIn(ExperimentalCoroutinesApi::class)
@SpringBootTest
@ActiveProfiles("test")
class AuthTokenServiceTest: IntegrationTest() {

    @Autowired
    private lateinit var sut: AuthTokenService

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
    fun `createToken is not the same for users`() {
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
    fun `isTokenValid with invalid tokens`() = runTest {
        // Arrange
        val token = ""
        // Act
        val result1 = sut.isTokenValid(token)
        // Asserts
        assertThat(result1, `is`(false))
    }
}
