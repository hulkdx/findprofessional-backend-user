package com.hulkdx.findprofessional

import com.hulkdx.findprofessional.model.Professional
import com.hulkdx.findprofessional.model.User
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface ProRepository : CoroutineCrudRepository<Professional, Int> {
    suspend fun findByEmail(email: String): Professional?
}
