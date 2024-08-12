package com.hulkdx.findprofessional.model

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Table("users")
data class User(
    @Id val id: Long? = null,
    val professionalId: Long? = null,
    val email: String,
    val password: String,
    val firstName: String,
    val lastName: String,
    val profileImage: String?,
    val skypeId: String?,
    @CreatedDate val createdAt: LocalDateTime?,
    @LastModifiedDate val updatedAt: LocalDateTime?,
): UserType
