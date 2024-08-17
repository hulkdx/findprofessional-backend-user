package com.hulkdx.findprofessional.utils

import com.hulkdx.findprofessional.model.request.RegisterRequest
import com.hulkdx.findprofessional.model.User


fun createUser(
    id: Long = 0,
    email: String = "",
    password: String = "",
    firstName: String = "",
    lastName: String = "",
) = User(
    id = id,
    email = email,
    password = password,
    firstName = firstName,
    lastName = lastName,
    profileImage = null,
    skypeId = null,
    createdAt = null,
    updatedAt = null,
)

fun createRegisterRequest(
    email: String = "kristopher.cleveland@example.com",
    password: String = "sit",
    firstName: String = "Audra Freeman",
    lastName: String = "Genaro McKee",
    skypeId: String? = null,
) = RegisterRequest(
    email = email,
    password = password,
    firstName = firstName,
    lastName = lastName,
    profileImage = null,
    skypeId = skypeId,
)
