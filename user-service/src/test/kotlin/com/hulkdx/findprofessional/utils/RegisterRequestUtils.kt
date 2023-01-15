package com.hulkdx.findprofessional.utils

import com.hulkdx.findprofessional.models.AuthRequest

fun createRegisterRequest(
    email: String = "test@email.com",
    password: String = "1234abdcx",
) = AuthRequest(
    email,
    password,
)
