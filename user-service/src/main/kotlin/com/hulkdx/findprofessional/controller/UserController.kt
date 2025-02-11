package com.hulkdx.findprofessional.controller

import com.hulkdx.findprofessional.model.ApiError
import com.hulkdx.findprofessional.model.request.RegisterRequest
import com.hulkdx.findprofessional.model.request.UserUpdateRequest
import com.hulkdx.findprofessional.service.AuthService
import com.hulkdx.findprofessional.service.TokenService
import com.hulkdx.findprofessional.service.UserService
import com.hulkdx.findprofessional.utils.Errors.INVALID_TOKEN_TYPE
import jakarta.validation.Valid
import org.springframework.data.r2dbc.config.EnableR2dbcAuditing
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.http.HttpStatus.OK
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(
    "/user",
    consumes = ["application/json"],
    produces = ["application/json"],
)
class UserController(
    private val authService: AuthService,
    private val tokenService: TokenService,
    private val userService: UserService,
) {
    @PostMapping("/")
    suspend fun updateUser(
        @RequestHeader(HttpHeaders.AUTHORIZATION) auth: String,
        @RequestBody @Valid body: UserUpdateRequest,
    ): ResponseEntity<*> {
        val accessToken = authService.getAccessToken(auth)
            ?: return ResponseEntity.status(BAD_REQUEST).body(ApiError(INVALID_TOKEN_TYPE))
        val jwt = tokenService.decodeJwt(accessToken)
        if (jwt == null || !tokenService.isTokenValid(jwt)) {
            return ResponseEntity.status(BAD_REQUEST).body(ApiError(INVALID_TOKEN_TYPE))
        }

        val userId = jwt.subject.toLongOrNull()
            ?: return ResponseEntity.status(HttpStatus.NOT_FOUND).build<Unit>()
        val updatedUser = userService.updateUser(userId, body)
            // It might be a professional user that tries to update user:
            ?: return ResponseEntity.status(HttpStatus.FORBIDDEN).build<Unit>()
        return ResponseEntity.status(OK).body(updatedUser)
    }
}
