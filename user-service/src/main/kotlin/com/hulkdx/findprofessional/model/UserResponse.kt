package com.hulkdx.findprofessional.model

data class UserResponse(
    val email: String,
    val firstName: String,
    val lastName: String,
    val profileImage: String?,
    val skypeId: String?,
): UserResponseType()
