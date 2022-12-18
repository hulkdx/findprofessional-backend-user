package com.hulkdx.findprofessional.utils

import org.springframework.security.crypto.password.PasswordEncoder

class TestPasswordEncoder : PasswordEncoder {
    override fun encode(rawPassword: CharSequence?): String {
        return "$rawPassword is from TestPasswordEncoder"
    }

    override fun matches(rawPassword: CharSequence?, encodedPassword: String?): Boolean {
        return encodedPassword == "$rawPassword is from TestPasswordEncoder"
    }
}
