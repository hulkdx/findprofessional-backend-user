package com.hulkdx.findprofessional.models

import java.time.LocalDateTime

data class RegisterRequest(
    val email: String,
    val password: String,
)
