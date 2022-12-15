package com.hulkdx.findprofessional

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PostMapping


@Controller("auth")
class AuthController {
    @PostMapping("login")
    suspend fun login() {
    }

    @PostMapping("register")
    suspend fun register() {
    }
}

interface UserRepository : CoroutineCrudRepository<User, Int>

@Table("users")
data class User(@Id val id: Int, val name: String)
