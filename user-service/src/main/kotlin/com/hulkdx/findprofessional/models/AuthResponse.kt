package com.hulkdx.findprofessional.models

data class AuthResponse(
    val token: TokenResponse,
    val user: UserResponse,
)
