package com.hulkdx.findprofessional.utils

import com.hulkdx.findprofessional.model.Professional
import com.hulkdx.findprofessional.model.User
import com.hulkdx.findprofessional.model.request.RegisterRequest
import com.hulkdx.findprofessional.model.request.UserUpdateRequest
import com.hulkdx.findprofessional.model.response.UserResponse
import jakarta.validation.constraints.Size
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
    firstName: String = "",
    lastName: String = "",
    coachType: String = "",
    priceNumber: Long? = null,
    priceCurrency: String? = null,
    profileImageUrl: String? = null,
    description: String? = null,
    pending: Boolean = false,
    createdAt: LocalDateTime? = LocalDateTime.now(),
    updatedAt: LocalDateTime? = LocalDateTime.now(),
) = Professional(
    id = id,
    email = email,
    password = password,
    firstName = firstName,
    lastName = lastName,
    coachType = coachType,
    priceNumber = priceNumber,
    priceCurrency = priceCurrency,
    profileImageUrl = profileImageUrl,
    description = description,
    pending = pending,
    createdAt = createdAt,
    updatedAt = updatedAt,
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
) = UserUpdateRequest(firstName, lastName, profileImage)

fun RegisterRequest.toUserResponse() = UserResponse(
    email = email,
    firstName = firstName,
    lastName = lastName,
    profileImage = profileImage,
)
