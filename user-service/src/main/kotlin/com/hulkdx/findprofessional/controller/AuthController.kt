package com.hulkdx.findprofessional.controller

import com.hulkdx.findprofessional.model.ApiError
import com.hulkdx.findprofessional.model.request.LoginRequest
import com.hulkdx.findprofessional.model.request.RefreshRequest
import com.hulkdx.findprofessional.model.request.RegisterRequest
import com.hulkdx.findprofessional.model.response.AuthResponse
import com.hulkdx.findprofessional.service.AuthService
import com.hulkdx.findprofessional.service.RefreshService
import com.hulkdx.findprofessional.service.TokenService
import com.hulkdx.findprofessional.utils.Errors.EMAIL_EXISTS
import com.hulkdx.findprofessional.utils.Errors.EMAIL_NOT_VALID
import com.hulkdx.findprofessional.utils.Errors.INVALID_TOKEN_TYPE
import com.hulkdx.findprofessional.utils.Errors.PASSWORD_NOT_VALID
import com.hulkdx.findprofessional.utils.Validator
import com.hulkdx.findprofessional.utils.toNormalUserResponse
import com.hulkdx.findprofessional.utils.toUserResponse
import jakarta.validation.Valid
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.data.r2dbc.config.EnableR2dbcAuditing
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.http.HttpStatus.CONFLICT
import org.springframework.http.HttpStatus.OK
import org.springframework.http.HttpStatus.UNAUTHORIZED
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

    @PostMapping("/register")
    suspend fun register(@RequestBody @Valid body: RegisterRequest): ResponseEntity<*> {
        if (!Validator.isEmailValid(body.email)) {
            return ResponseEntity.status(BAD_REQUEST)
                .body(ApiError(EMAIL_NOT_VALID))
        }
        if (!Validator.isPasswordValid(body.password)) {
            return ResponseEntity.status(BAD_REQUEST)
                .body(ApiError(PASSWORD_NOT_VALID))
        }
        return try {
            val user = authService.register(body)
            val token = tokenService.createToken(user)
            ResponseEntity.status(OK)
                .body(AuthResponse(token, user.toNormalUserResponse()))
        } catch (e: DataIntegrityViolationException) {
            ResponseEntity.status(CONFLICT)
                .body(ApiError(EMAIL_EXISTS))
        }
    }

    @PostMapping("/login")
    suspend fun login(@RequestBody @Valid body: LoginRequest): ResponseEntity<*> {
        if (!Validator.isEmailValid(body.email)) {
            return ResponseEntity.status(BAD_REQUEST)
                .body(ApiError(EMAIL_NOT_VALID))
        }
        val user = authService.login(body)
        return if (user != null) {
            val token = tokenService.createToken(user)
            ResponseEntity.status(OK)
                .body(AuthResponse(token, user.toUserResponse()))
        } else {
            ResponseEntity.status(UNAUTHORIZED).build<Unit>()
        }
    }

    @PostMapping("/refresh")
    suspend fun refresh(
        @RequestHeader(HttpHeaders.AUTHORIZATION) auth: String,
        @RequestBody @Valid request: RefreshRequest,
    ): ResponseEntity<*> {
        val accessToken = authService.getAccessToken(auth)
            ?: return ResponseEntity.status(BAD_REQUEST).body(ApiError(INVALID_TOKEN_TYPE))

        val body = refreshService.refreshToken(accessToken, request.refreshToken)
        return if (body == null) {
            ResponseEntity.status(UNAUTHORIZED).build<Unit>()
        } else {
            ResponseEntity.status(OK)
                .body(body)
        }
    }
}
