package com.hulkdx.findprofessional.utils

import com.hulkdx.findprofessional.models.RegisterRequest

fun createRegisterRequest(
    email: String = "test@email.com",
    password: String = "1234abdcx",
) = RegisterRequest(
    email,
    password,
)
