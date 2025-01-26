package com.hulkdx.findprofessional.repository

import com.hulkdx.findprofessional.model.User
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface UserRepository : CoroutineCrudRepository<User, Int> {
    suspend fun findByEmail(email: String): User?
}
