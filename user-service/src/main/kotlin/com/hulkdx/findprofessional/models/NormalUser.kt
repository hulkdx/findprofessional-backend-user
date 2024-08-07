package com.hulkdx.findprofessional.models

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Table("users")
data class NormalUser(
    val email: String,
    val password: String,
    val firstName: String,
    val lastName: String,
    val profileImage: String? = null,
    val skypeId: String? = null,
    @Id val id: Int? = null,
    @CreatedDate val createdAt: LocalDateTime? = null,
    @LastModifiedDate val updatedAt: LocalDateTime? = null,
): User
