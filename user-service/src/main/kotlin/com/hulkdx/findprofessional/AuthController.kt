package com.hulkdx.findprofessional

import com.hulkdx.findprofessional.model.RegisterRequest
import com.hulkdx.findprofessional.model.AuthResponse
import com.hulkdx.findprofessional.model.LoginRequest
import com.hulkdx.findprofessional.model.RefreshRequest
import com.hulkdx.findprofessional.utils.toNormalUserResponse
import com.hulkdx.findprofessional.utils.R
import com.hulkdx.findprofessional.utils.Validator
import com.hulkdx.findprofessional.utils.toUserResponse
import jakarta.validation.Valid
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
// EnableR2dbcAuditing: required for createdAt and updatedAt
@EnableR2dbcAuditing
class AuthController(
    private val authService: AuthService,
    private val tokenService: TokenService,
    private val refreshService: RefreshService,
) {
    private val emailNotValid = "Email is not valid"
    private val passwordNotValid = "Password is not valid"
    private val emailExists = "Email already exists"
    private val invalidTokenType = "Invalid token type"

    @PostMapping("/register")
    suspend fun register(@RequestBody @Valid body: RegisterRequest): ResponseEntity<*> {
        if (!Validator.isEmailValid(body.email)) {
            return R.badRequest(emailNotValid)
        }
        if (!Validator.isPasswordValid(body.password)) {
            return R.badRequest(passwordNotValid)
        }
        return try {
            val user = authService.register(body)
            val token = tokenService.createToken(user)
            R.ok(body = AuthResponse(token, user.toNormalUserResponse()))
        } catch (e: DataIntegrityViolationException) {
            R.conflict(emailExists)
        }
    }

    @PostMapping("/login")
    suspend fun login(@RequestBody @Valid body: LoginRequest): ResponseEntity<*> {
        if (!Validator.isEmailValid(body.email)) {
            return R.badRequest(emailNotValid)
        }
        val user = authService.login(body)
        return if (user != null) {
            val token = tokenService.createToken(user)
            R.ok(body = AuthResponse(token, user.toUserResponse()))
        } else {
            R.unauthorized()
        }
    }

    @PostMapping("/refresh")
    suspend fun refresh(
        @RequestHeader(HttpHeaders.AUTHORIZATION) auth: String,
        @RequestBody @Valid request: RefreshRequest,
    ): ResponseEntity<*> {
        val authSplit = auth.split(" ")
        if (authSplit.size != 2) {
            return R.badRequest(invalidTokenType)
        }
        val (authType, accessToken) = authSplit
        if (authType != "Bearer") {
            return R.badRequest(invalidTokenType)
        }

        val body = refreshService.refreshToken(accessToken, request.refreshToken)
        return if (body == null) {
            R.unauthorized()
        } else {
            R.ok(body)
        }
    }
}
