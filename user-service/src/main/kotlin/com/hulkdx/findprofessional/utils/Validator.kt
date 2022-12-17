package com.hulkdx.findprofessional.utils


object Validator {
    fun isEmailValid(email: String): Boolean {
        // https://emailregex.com/
        return "(?:[a-z0-9!#\$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#\$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])"
            .toRegex()
            .matches(email)
    }

    fun isPasswordValid(password: String): Boolean {
        if (password.length < 8) {
            return false
        }
        val containsNumbers = "\\d+".toRegex().containsMatchIn(password)
        val containsLetters = "[a-zA-Z]+".toRegex().containsMatchIn(password)
        val containsSpecials = "[ !@#\$%^&*_+-]+".toRegex().containsMatchIn(password)
        val security = listOf(containsNumbers, containsLetters, containsSpecials)
            .sumOf { if (it) 1L else 0L }
        return security > 1
    }
}
