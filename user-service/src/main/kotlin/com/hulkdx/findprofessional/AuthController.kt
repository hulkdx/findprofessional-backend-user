package com.hulkdx.findprofessional

import com.hulkdx.findprofessional.utils.Validator
import com.hulkdx.findprofessional.utils.toResponseEntity
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
    suspend fun register(@RequestBody body: User): ResponseEntity<*> {
        if (!Validator.isEmailValid(body.email)) {
            return HttpStatus.BAD_REQUEST.toResponseEntity("Email is not valid")
        }
        return try {
            userRepository.save(body)
            HttpStatus.CREATED.toResponseEntity()
        } catch (e: DataIntegrityViolationException) {
            HttpStatus.CONFLICT.toResponseEntity()
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
