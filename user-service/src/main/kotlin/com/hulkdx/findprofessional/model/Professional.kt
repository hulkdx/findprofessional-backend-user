package com.hulkdx.findprofessional.model

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Table(name = "professionals")
data class Professional(
    @Id val id: Long? = null,
    val email: String,
    val password: String,
    val firstName: String,
    val lastName: String,
    val coachType: String,
    val priceNumber: Long? = null,
    val priceCurrency: String? = null,
    val profileImageUrl: String? = null,
    val description: String? = null,
    val pending: Boolean = false,
    @CreatedDate val createdAt: LocalDateTime?,
    @LastModifiedDate val updatedAt: LocalDateTime?,
): UserType
