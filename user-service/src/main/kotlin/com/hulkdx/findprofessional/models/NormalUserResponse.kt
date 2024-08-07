package com.hulkdx.findprofessional.models

data class NormalUserResponse(
    val email: String,
    val firstName: String,
    val lastName: String,
    val profileImage: String?,
    val skypeId: String?,
): UserResponse()
