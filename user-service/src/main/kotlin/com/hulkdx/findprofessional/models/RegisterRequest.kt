package com.hulkdx.findprofessional.models

import jakarta.validation.constraints.Size

data class RegisterRequest(
    @field:Size(max = 50) val email: String,
    @field:Size(max = 50) val password: String,
    @field:Size(max = 50) val firstName: String,
    @field:Size(max = 50) val lastName: String,
    @field:Size(max = 50) val profileImage: String?,
    @field:Size(max = 50) val skypeId: String?,
)

fun RegisterRequest.toUser(password: String): NormalUser {
    return NormalUser(
        email = email,
        password = password,
        firstName = firstName,
        lastName = lastName,
        profileImage = profileImage,
        skypeId = skypeId,
        createdAt = null,
        updatedAt = null,
    )
}
