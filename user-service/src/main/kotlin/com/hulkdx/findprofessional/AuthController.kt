package com.hulkdx.findprofessional

import com.hulkdx.findprofessional.models.RegisterRequest
import com.hulkdx.findprofessional.models.User
import com.hulkdx.findprofessional.utils.R
import com.hulkdx.findprofessional.utils.Validator
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.data.r2dbc.config.EnableR2dbcAuditing
import org.springframework.http.ResponseEntity
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(
    "/auth",
    consumes = ["application/json"],
    produces = ["application/json"],
)
@EnableR2dbcAuditing
class AuthController(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
) {
    @PostMapping("/register")
    suspend fun register(@RequestBody body: RegisterRequest): ResponseEntity<*> {
        if (!Validator.isEmailValid(body.email)) {
            return R.badRequest("Email not valid")
        }
        if (!Validator.isPasswordValid(body.password)) {
            return R.badRequest("Password not valid")
        }
        return try {
            val password = passwordEncoder.encode(body.password)
            val user = User(body.email, password)
            userRepository.save(user)
            R.created()
        } catch (e: DataIntegrityViolationException) {
            R.conflict()
        }
    }

    @PostMapping("/login")
    suspend fun login(@RequestBody body: RegisterRequest): ResponseEntity<*> {
        val user = userRepository.findByEmail(body.email) ?: return R.conflict()
        // TODO:
        return R.conflict()
    }
}
