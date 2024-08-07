package com.hulkdx.findprofessional.models

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "type"
)
@JsonSubTypes(
    JsonSubTypes.Type(value = NormalUserResponse::class, name = "normal")
)
sealed class UserResponse

data class NormalUserResponse(
    val email: String,
    val firstName: String,
    val lastName: String,
    val profileImage: String?,
    val skypeId: String?,
): UserResponse()

