package com.hulkdx.findprofessional.models

data class UserResponse(
    val email: String,
)

fun User.toUserResponse(): UserResponse {
    return UserResponse(email)
}
