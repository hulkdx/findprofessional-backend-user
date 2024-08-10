package com.hulkdx.findprofessional.model

data class AuthResponse(
    val token: TokenResponse,
    val user: UserResponseType,
)
