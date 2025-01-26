package com.hulkdx.findprofessional.repository

import com.hulkdx.findprofessional.model.Professional
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface ProRepository : CoroutineCrudRepository<Professional, Int> {
    suspend fun findByEmail(email: String): Professional?
}
