package com.hulkdx.findprofessional

import com.hulkdx.findprofessional.models.LoginRequest
import com.hulkdx.findprofessional.models.RegisterRequest
import com.hulkdx.findprofessional.models.NormalUser
import com.hulkdx.findprofessional.models.toUser
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class AuthService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
) {
    suspend fun register(body: RegisterRequest): NormalUser {
        val password = passwordEncoder.encode(body.password)
        val user = body.toUser(password)
        userRepository.save(user)
        return user
    }

    suspend fun login(body: LoginRequest): NormalUser? {
        val user = userRepository.findByEmail(body.email) ?: return null
        val matches = passwordEncoder.matches(body.password, user.password)
        if (!matches) {
            return null
        }
        return user
    }
}
