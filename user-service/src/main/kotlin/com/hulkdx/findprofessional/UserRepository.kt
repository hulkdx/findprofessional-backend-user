package com.hulkdx.findprofessional

import com.hulkdx.findprofessional.models.NormalUser
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface UserRepository : CoroutineCrudRepository<NormalUser, Int> {
    suspend fun findByEmail(email: String): NormalUser?
}
