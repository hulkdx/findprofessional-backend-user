package com.hulkdx.findprofessional.utils

import com.hulkdx.findprofessional.models.User


fun createUser(userId: Int): User {
    return User(
        email = "",
        password = "",
        id = userId,
    )
}
