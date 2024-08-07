package com.hulkdx.findprofessional.utils

import com.hulkdx.findprofessional.models.NormalUserResponse
import com.hulkdx.findprofessional.models.NormalUser

fun NormalUser.toNormalUserResponse(): NormalUserResponse {
    return NormalUserResponse(
        email,
        firstName,
        lastName,
        profileImage,
        skypeId,
    )
}
