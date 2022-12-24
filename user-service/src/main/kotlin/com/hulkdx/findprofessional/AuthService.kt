package com.hulkdx.findprofessional

import com.hulkdx.findprofessional.models.RegisterRequest
import com.hulkdx.findprofessional.models.User
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

    suspend fun login(body: RegisterRequest): User? {
        val user = userRepository.findByEmail(body.email) ?: return null
        val matches = passwordEncoder.matches(body.password, user.password)
        if (!matches) {
            return null
        }
        return user
    }
}
