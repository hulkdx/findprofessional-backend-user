package com.hulkdx.findprofessional.models

import jakarta.validation.constraints.Size

data class LoginRequest(
    @field:Size(max = 50) val email: String,
    @field:Size(max = 50) val password: String,
)
