package com.hulkdx.findprofessional.model.response

data class AuthResponse(
    val token: TokenResponse,
    val user: UserResponseType,
)
