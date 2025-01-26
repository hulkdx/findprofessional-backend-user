package com.hulkdx.findprofessional

import com.hulkdx.findprofessional.model.request.LoginRequest
import com.hulkdx.findprofessional.repository.ProRepository
import com.hulkdx.findprofessional.repository.UserRepository
import com.hulkdx.findprofessional.service.AuthService
import com.hulkdx.findprofessional.utils.createPro
import com.hulkdx.findprofessional.utils.createUser
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.condition.DisabledInNativeImage
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.whenever
import org.springframework.security.crypto.password.PasswordEncoder

@ExtendWith(MockitoExtension::class)
@DisabledInNativeImage
class AuthServiceTest {

    private lateinit var sut: AuthService

    @Mock
    private lateinit var userRepository: UserRepository
    @Mock
    private lateinit var professionalRepository: ProRepository
    @Mock
    private lateinit var passwordEncoder: PasswordEncoder

    @BeforeEach
    fun setUp() {
        sut = AuthService(userRepository, professionalRepository, passwordEncoder)
    }

    @Test
    fun `login when when no user found return null`() = runTest {
        // Arrange
        val email = "email"
        val password = "pass"
        val request = LoginRequest(email, password)

        whenever(userRepository.findByEmail(email)).thenReturn(null)
        // Act
        val result = sut.login(request)
        // Assert
        assertNull(result)
    }

    @Test
    fun `login when it is a normal user return normal user`() = runTest {
        // Arrange
        val email = "email"
        val password = "pass"
        val professionalId = null
        val user = createUser(email = email, password = password, professionalId = professionalId)
        val request = LoginRequest(email, password)

        whenever(userRepository.findByEmail(email)).thenReturn(user)
        whenever(passwordEncoder.matches(password, password)).thenReturn(true)
        // Act
        val result = sut.login(request)
        // Assert
        assertThat(result, `is`(user))
    }

    @Test
    fun `login when it is a pro user return normal user`() = runTest {
        // Arrange
        val email = "email"
        val password = "pass"
        val professionalId = 5L
        val user = createUser(email = email, password = password, professionalId = professionalId)
        val pro = createPro(id = professionalId, email = email, password = password)
        val request = LoginRequest(email, password)

        whenever(userRepository.findByEmail(email)).thenReturn(user)
        whenever(professionalRepository.findByEmail(email)).thenReturn(pro)
        whenever(passwordEncoder.matches(password, password)).thenReturn(true)
        // Act
        val result = sut.login(request)
        // Assert
        assertThat(result, `is`(pro))
    }
}
