package com.hulkdx.findprofessional.utils

import com.hulkdx.findprofessional.models.NormalUserResponse
import com.hulkdx.findprofessional.models.NormalUser
import com.hulkdx.findprofessional.models.User
import com.hulkdx.findprofessional.models.UserResponse

fun User.toUserResponse(): UserResponse {
    return when (this) {
        is NormalUser -> toNormalUserResponse()
    }
}

fun NormalUser.toNormalUserResponse(): NormalUserResponse {
    return NormalUserResponse(
        email,
        firstName,
        lastName,
        profileImage,
        skypeId,
    )
}
