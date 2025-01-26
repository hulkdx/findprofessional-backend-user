package com.hulkdx.findprofessional.service

import com.hulkdx.findprofessional.model.ApiError
import com.hulkdx.findprofessional.repository.ProRepository
import com.hulkdx.findprofessional.repository.UserRepository
import com.hulkdx.findprofessional.model.User
import com.hulkdx.findprofessional.model.UserType
import com.hulkdx.findprofessional.model.request.LoginRequest
import com.hulkdx.findprofessional.model.request.RegisterRequest
import com.hulkdx.findprofessional.model.request.toUser
import com.hulkdx.findprofessional.utils.Errors.INVALID_TOKEN_TYPE
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class AuthService(
    private val userRepository: UserRepository,
    private val professionalRepository: ProRepository,
    private val passwordEncoder: PasswordEncoder,
) {
    suspend fun register(body: RegisterRequest): User {
        val password = passwordEncoder.encode(body.password)
        val user = body.toUser(password)
        userRepository.save(user)
        return user
    }

    suspend fun login(body: LoginRequest): UserType? {
        val user = userRepository.findByEmail(body.email) ?: return null
        if (user.professionalId != null) {
            return loginPro(body)
        }
        val matches = passwordEncoder.matches(body.password, user.password)
        if (!matches) {
            return null
        }
        return user
    }

    private suspend fun loginPro(body: LoginRequest): UserType? {
        val user = professionalRepository.findByEmail(body.email) ?: return null
        val matches = passwordEncoder.matches(body.password, user.password)
        if (!matches) {
            return null
        }
        return user
    }

    fun getAccessToken(auth: String): String? {
        val authSplit = auth.split(" ")
        if (authSplit.size != 2) {
            return null
        }
        val (authType, accessToken) = authSplit
        if (authType != "Bearer") {
            return null
        }
        return accessToken
    }
}
