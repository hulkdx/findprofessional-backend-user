package com.hulkdx.findprofessional

import com.hulkdx.findprofessional.models.RegisterRequest
import com.hulkdx.findprofessional.utils.R
import com.hulkdx.findprofessional.utils.UserNotFoundException
import com.hulkdx.findprofessional.utils.Validator
import jakarta.validation.Valid
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.data.r2dbc.config.EnableR2dbcAuditing
import org.springframework.http.ResponseEntity
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
    private val authService: AuthService,
) {
    @PostMapping("/register")
    suspend fun register(@RequestBody @Valid body: RegisterRequest): ResponseEntity<*> {
        if (!Validator.isEmailValid(body.email)) {
            return R.badRequest("Email not valid")
        }
        if (!Validator.isPasswordValid(body.password)) {
            return R.badRequest("Password not valid")
        }
        return try {
            authService.register(body)
            R.created()
        } catch (e: DataIntegrityViolationException) {
            R.conflict("email exists")
        }
    }

    @PostMapping("/login")
    suspend fun login(@RequestBody @Valid body: RegisterRequest): ResponseEntity<*> {
        return try {
            authService.login(body)
            R.created()
        } catch (e: UserNotFoundException) {
            R.conflict()
        }
    }
}
