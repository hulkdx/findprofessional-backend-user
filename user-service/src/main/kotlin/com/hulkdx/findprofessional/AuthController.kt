package com.hulkdx.findprofessional

import com.hulkdx.findprofessional.models.RegisterRequest
import com.hulkdx.findprofessional.utils.R
import com.hulkdx.findprofessional.utils.Validator
import jakarta.validation.Valid
import jakarta.validation.constraints.Size
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.data.r2dbc.config.EnableR2dbcAuditing
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
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
    private val tokenService: TokenService,
) {
    private val emailNotValid = "Email is not valid"
    private val passwordNotValid = "Password is not valid"
    private val emailExists = "Email already exists"

    @PostMapping("/register")
    suspend fun register(@RequestBody @Valid body: RegisterRequest): ResponseEntity<*> {
        if (!Validator.isEmailValid(body.email)) {
            return R.badRequest(emailNotValid)
        }
        if (!Validator.isPasswordValid(body.password)) {
            return R.badRequest(passwordNotValid)
        }
        return try {
            authService.register(body)
            R.created()
        } catch (e: DataIntegrityViolationException) {
            R.conflict(emailExists)
        }
    }

    @PostMapping("/login")
    suspend fun login(@RequestBody @Valid body: RegisterRequest): ResponseEntity<*> {
        if (!Validator.isEmailValid(body.email)) {
            return R.badRequest(emailNotValid)
        }
        val user = authService.login(body)
        return if (user != null) {
            R.ok(body = tokenService.createToken(user))
        } else {
            R.unauthorized()
        }
    }

    @PostMapping("/refresh")
    suspend fun refresh(
        @RequestHeader(HttpHeaders.AUTHORIZATION) auth: String,
        @RequestBody @Valid @Size(max = 50) refreshToken: String,
    ) {
        TODO("implement")
    }
}
