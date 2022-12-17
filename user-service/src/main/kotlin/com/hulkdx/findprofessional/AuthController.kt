package com.hulkdx.findprofessional

import org.springframework.dao.DataIntegrityViolationException
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("/auth")
class AuthController(
    private val userRepository: UserRepository,
) {

    @PostMapping("/register")
    suspend fun register(@RequestBody body: User): ResponseEntity<Any> {
        return try {
            userRepository.save(body)
            ResponseEntity.status(HttpStatus.CREATED).build()
        } catch (e: DataIntegrityViolationException) {
            ResponseEntity.status(HttpStatus.CONFLICT).build()
        }
    }
}

interface UserRepository : CoroutineCrudRepository<User, Int>

@Table("users")
data class User(
    val email: String,
    val password: String,
    @Id val id: Int? = null,
)
