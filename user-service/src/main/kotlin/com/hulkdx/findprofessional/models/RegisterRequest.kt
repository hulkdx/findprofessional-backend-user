package com.hulkdx.findprofessional.models

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

data class RegisterRequest(
    val email: String,
    val password: String,
)
