package com.hulkdx.findprofessional.utils

import com.hulkdx.findprofessional.models.RegisterRequest
import com.hulkdx.findprofessional.models.User


fun createUser(
    id: Int = 0,
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
)

fun createRegisterRequest(
    email: String = "kristopher.cleveland@example.com",
    password: String = "sit",
    firstName: String = "Audra Freeman",
    lastName: String = "Genaro McKee"
) = RegisterRequest(
    email = email,
    password = password,
    firstName = firstName,
    lastName = lastName,
    profileImage = null,
)
