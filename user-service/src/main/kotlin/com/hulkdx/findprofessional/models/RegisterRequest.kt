package com.hulkdx.findprofessional.models

import jakarta.validation.constraints.Size

data class RegisterRequest(
    @field:Size(max = 50)
    val email: String,
    @field:Size(max = 50)
    val password: String,
)
