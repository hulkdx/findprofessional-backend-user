package com.hulkdx.findprofessional.utils

import com.hulkdx.findprofessional.models.RegisterRequest
import com.hulkdx.findprofessional.models.User
import com.hulkdx.findprofessional.models.NormalUser


fun createUser(
    id: Int = 0,
    email: String = "",
    password: String = "",
    firstName: String = "",
    lastName: String = "",
    skypeId: String? = null,
) = User(
    id = id,
    email = email,
    password = password,
    firstName = firstName,
    lastName = lastName,
    skypeId = skypeId,
)

fun createRegisterRequest(
    email: String = "kristopher.cleveland@example.com",
    password: String = "1234abdcx",
    firstName: String = "Audra Freeman",
    lastName: String = "Genaro McKee",
    profileImage: String? = null,
    skypeId: String? = null,
) = RegisterRequest(
    email = email,
    password = password,
    firstName = firstName,
    lastName = lastName,
    profileImage = profileImage,
    skypeId = skypeId,
)

fun RegisterRequest.toUserResponse() = NormalUser(
    email = email,
    firstName = firstName,
    lastName = lastName,
    profileImage = profileImage,
    skypeId = skypeId,
)
