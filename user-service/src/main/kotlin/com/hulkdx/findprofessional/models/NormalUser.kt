package com.hulkdx.findprofessional.models

data class NormalUser(
    val email: String,
    val firstName: String,
    val lastName: String,
    val profileImage: String?,
    val skypeId: String?,
)

fun User.toUserResponse(): NormalUser {
    return NormalUser(
        email,
        firstName,
        lastName,
        profileImage,
        skypeId,
    )
}
