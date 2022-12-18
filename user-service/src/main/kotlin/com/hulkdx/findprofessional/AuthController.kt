package com.hulkdx.findprofessional

import com.hulkdx.findprofessional.models.User
import com.hulkdx.findprofessional.utils.R
import com.hulkdx.findprofessional.utils.Validator
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.http.ResponseEntity
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("/auth")
class AuthController(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder = BCryptPasswordEncoder(),
) {

    @PostMapping("/register")
    suspend fun register(@RequestBody body: User): ResponseEntity<*> {
        if (!Validator.isEmailValid(body.email)) {
            return R.badRequest("Email not valid")
        }
        if (!Validator.isPasswordValid(body.password)) {
            return R.badRequest("Password not valid")
        }
        if (body.id != null) {
            return R.badRequest("")
        }
        return try {
            val user = body.copy(
                password = passwordEncoder.encode(body.password),
            )
            userRepository.save(user)
            R.created()
        } catch (e: DataIntegrityViolationException) {
            R.conflict()
        }
    }
}
