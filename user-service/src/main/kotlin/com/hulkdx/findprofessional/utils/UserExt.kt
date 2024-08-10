package com.hulkdx.findprofessional.utils

import com.hulkdx.findprofessional.model.response.UserResponse
import com.hulkdx.findprofessional.model.User
import com.hulkdx.findprofessional.model.UserType
import com.hulkdx.findprofessional.model.response.UserResponseType

fun UserType.toUserResponse(): UserResponseType {
    return when (this) {
        is User -> toNormalUserResponse()
    }
}

fun User.toNormalUserResponse(): UserResponse {
    return UserResponse(
        email,
        firstName,
        lastName,
        profileImage,
        skypeId,
    )
}
