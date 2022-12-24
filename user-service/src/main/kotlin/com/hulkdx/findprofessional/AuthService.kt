package com.hulkdx.findprofessional

import com.hulkdx.findprofessional.models.RegisterRequest
import com.hulkdx.findprofessional.models.User
import com.hulkdx.findprofessional.utils.UserNotFoundException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class AuthService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
) {
    suspend fun register(body: RegisterRequest) {
        val password = passwordEncoder.encode(body.password)
        val user = User(body.email, password)
        userRepository.save(user)
    }

    suspend fun login(body: RegisterRequest) {
        val user = userRepository.findByEmail(body.email) ?: throw UserNotFoundException()
    }
}
