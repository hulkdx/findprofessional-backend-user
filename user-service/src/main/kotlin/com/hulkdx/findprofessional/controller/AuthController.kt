package com.hulkdx.findprofessional.controller

import com.hulkdx.findprofessional.model.ApiError
import com.hulkdx.findprofessional.model.request.LoginRequest
import com.hulkdx.findprofessional.model.request.RefreshRequest
import com.hulkdx.findprofessional.model.request.RegisterRequest
import com.hulkdx.findprofessional.model.request.UserUpdateRequest
import com.hulkdx.findprofessional.model.response.AuthResponse
import com.hulkdx.findprofessional.model.response.TokenResponse
import com.hulkdx.findprofessional.model.response.UserResponse
import com.hulkdx.findprofessional.service.AuthService
import com.hulkdx.findprofessional.service.RefreshService
import com.hulkdx.findprofessional.service.TokenService
import com.hulkdx.findprofessional.service.UserService
import com.hulkdx.findprofessional.utils.Errors.EMAIL_EXISTS
import com.hulkdx.findprofessional.utils.Errors.EMAIL_NOT_VALID
import com.hulkdx.findprofessional.utils.Errors.INVALID_TOKEN_TYPE
import com.hulkdx.findprofessional.utils.Errors.PASSWORD_NOT_VALID
import com.hulkdx.findprofessional.utils.Validator
import com.hulkdx.findprofessional.utils.toNormalUserResponse
import com.hulkdx.findprofessional.utils.toUserResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
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
@Tag(name = "Authentication")
class AuthController(
    private val authService: AuthService,
    private val tokenService: TokenService,
    private val refreshService: RefreshService,
    private val userService: UserService,
) {

    @Operation(summary = "Register a normal user")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "User registered",
                content = [Content(schema = Schema(implementation = AuthResponse::class))]
            ),
            ApiResponse(
                responseCode = "400",
                description = "Invalid email or password",
                content = [Content(schema = Schema(implementation = ApiError::class))]
            ),
            ApiResponse(
                responseCode = "409",
                description = "Email already exists",
                content = [Content(schema = Schema(implementation = ApiError::class))]
            ),
        ]
    )
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

    @Operation(summary = "Log in with email and password")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Login successful",
                content = [Content(schema = Schema(implementation = AuthResponse::class))]
            ),
            ApiResponse(
                responseCode = "400",
                description = "Invalid email",
                content = [Content(schema = Schema(implementation = ApiError::class))]
            ),
            ApiResponse(
                responseCode = "401",
                description = "Invalid credentials",
                content = [Content()]
            ),
        ]
    )
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

    @Operation(
        summary = "Refresh an access token",
        security = [SecurityRequirement(name = "bearerAuth")]
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Token refreshed",
                content = [Content(schema = Schema(implementation = TokenResponse::class))]
            ),
            ApiResponse(
                responseCode = "400",
                description = "Invalid authorization header",
                content = [Content(schema = Schema(implementation = ApiError::class))]
            ),
            ApiResponse(
                responseCode = "401",
                description = "Invalid refresh token",
                content = [Content()]
            ),
        ]
    )
    @PostMapping("/refresh")
    suspend fun refresh(
        @Parameter(hidden = true)
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

    @Operation(
        summary = "Update the current normal user profile",
        security = [SecurityRequirement(name = "bearerAuth")]
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "User updated",
                content = [Content(schema = Schema(implementation = UserResponse::class))]
            ),
            ApiResponse(
                responseCode = "400",
                description = "Invalid token",
                content = [Content(schema = Schema(implementation = ApiError::class))]
            ),
            ApiResponse(
                responseCode = "403",
                description = "Professional users cannot update this profile",
                content = [Content()]
            ),
            ApiResponse(
                responseCode = "404",
                description = "User not found",
                content = [Content()]
            ),
        ]
    )
    @PostMapping("/user")
    suspend fun updateUser(
        @Parameter(hidden = true)
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
