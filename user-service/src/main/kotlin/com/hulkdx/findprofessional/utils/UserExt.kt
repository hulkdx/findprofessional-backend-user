package com.hulkdx.findprofessional.utils

import com.hulkdx.findprofessional.models.NormalUserResponse
import com.hulkdx.findprofessional.models.User

fun User.toNormalUserResponse(): NormalUserResponse {
    return NormalUserResponse(
        email,
        firstName,
        lastName,
        profileImage,
        skypeId,
    )
}
