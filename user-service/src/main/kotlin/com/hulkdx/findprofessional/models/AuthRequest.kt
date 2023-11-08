package com.hulkdx.findprofessional.models

import jakarta.validation.constraints.Size

data class AuthRequest(
    @field:Size(max = 50)
    val email: String,
    @field:Size(max = 50)
    val password: String,
    @field:Size(max = 50)
    val firstName: String,
    @field:Size(max = 50)
    val lastName: String,
    @field:Size(max = 50)
    val profileImage: String?,
)

fun AuthRequest.toUser(password: String): User {
    return User(
        email = email,
        password = password,
        firstName = firstName,
        lastName = lastName,
        profileImage = profileImage,
    )
}
