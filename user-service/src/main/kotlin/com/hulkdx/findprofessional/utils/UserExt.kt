package com.hulkdx.findprofessional.utils

import com.hulkdx.findprofessional.model.Professional
import com.hulkdx.findprofessional.model.User
import com.hulkdx.findprofessional.model.UserType
import com.hulkdx.findprofessional.model.response.ProfessionalResponse
import com.hulkdx.findprofessional.model.response.UserResponse
import com.hulkdx.findprofessional.model.response.UserResponseType

fun UserType.toUserResponse(): UserResponseType {
    return when (this) {
        is User -> toNormalUserResponse()
        is Professional -> toProUserResponse()
    }
}

fun User.toNormalUserResponse() = UserResponse(
    email = email,
    firstName = firstName,
    lastName = lastName,
    profileImage = profileImage,
    skypeId = skypeId,
)

private fun Professional.toProUserResponse() = ProfessionalResponse(
    email = email,
    firstName = firstName,
    lastName = lastName,
    coachType = coachType,
    priceNumber = priceNumber,
    priceCurrency = priceCurrency,
    profileImageUrl = profileImageUrl,
    description = description,
    skypeId = skypeId,
)
