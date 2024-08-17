package com.hulkdx.findprofessional.model.response

data class ProfessionalResponse(
    val email: String,
    val firstName: String,
    val lastName: String,
    val coachType: String,
    val priceNumber: Long? = null,
    val priceCurrency: String? = null,
    val profileImageUrl: String? = null,
    val description: String? = null,
    val skypeId: String? = null,
) : UserResponseType()
