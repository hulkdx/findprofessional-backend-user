package com.hulkdx.findprofessional.models

data class UserResponse(
    val email: String,
    val firstName: String,
    val lastName: String,
    val profileImage: String?,
)

fun User.toUserResponse(): UserResponse {
    return UserResponse(
        email,
        firstName,
        lastName,
        profileImage,
    )
}
