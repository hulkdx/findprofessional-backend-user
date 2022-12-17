package com.hulkdx.findprofessional

import com.hulkdx.findprofessional.models.User
import com.hulkdx.findprofessional.utils.R
import com.hulkdx.findprofessional.utils.Validator
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("/auth")
class AuthController(
    private val userRepository: UserRepository,
) {

    @PostMapping("/register")
    suspend fun register(@RequestBody body: User): ResponseEntity<*> {
        if (!Validator.isEmailValid(body.email)) {
            return R.badRequest("Email is not valid")
        }
        return try {
            userRepository.save(body)
            R.created()
        } catch (e: DataIntegrityViolationException) {
            R.conflict()
        }
    }
}
