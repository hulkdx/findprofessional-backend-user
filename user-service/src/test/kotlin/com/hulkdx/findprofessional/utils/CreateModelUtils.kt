package com.hulkdx.findprofessional.utils

import com.hulkdx.findprofessional.model.Professional
import com.hulkdx.findprofessional.model.User
import com.hulkdx.findprofessional.model.request.RegisterRequest
import com.hulkdx.findprofessional.model.request.UserUpdateRequest
import java.time.LocalDateTime


fun createUser(
    id: Long? = 0,
    professionalId: Long? = null,
    email: String = "",
    password: String = "",
    firstName: String = "",
    lastName: String = "",
) = User(
    id = id,
    professionalId = professionalId,
    email = email,
    password = password,
    firstName = firstName,
    lastName = lastName,
    createdAt = null,
    updatedAt = null,
    profileImage = null,
)

fun createPro(
    id: Long? = null,
    email: String = "",
    password: String = "",
) = Professional(
    id = id,
    email = email,
    password = password,
    firstName = "",
    lastName = "",
    coachType = "",
    createdAt = LocalDateTime.now(),
    updatedAt = LocalDateTime.now(),
)

fun createRegisterRequest(
    email: String = "kristopher.cleveland@example.com",
    password: String = "1234abdcx",
    firstName: String = "Audra Freeman",
    lastName: String = "Genaro McKee",
    profileImage: String? = null,
) = RegisterRequest(
    email = email,
    password = password,
    firstName = firstName,
    lastName = lastName,
    profileImage = profileImage,
)

fun createUserUpdateRequest(
    firstName: String = "Audra Freeman",
    lastName: String = "Genaro McKee",
    profileImage: String? = null,
) = UserUpdateRequest(
    firstName = firstName,
    lastName = lastName,
    profileImage = profileImage
)
