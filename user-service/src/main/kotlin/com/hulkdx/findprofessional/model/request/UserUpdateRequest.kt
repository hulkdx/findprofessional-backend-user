package com.hulkdx.findprofessional.model.request

import jakarta.validation.constraints.Size

data class UserUpdateRequest(
    @field:Size(max = 50) val firstName: String?,
    @field:Size(max = 50) val lastName: String?,
    @field:Size(max = 50) val profileImage: String?,
)
