package com.hulkdx.findprofessional.models

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

@Table("users")
data class User(
    val email: String,
    val password: String,
    @Id val id: Int? = null,
)
