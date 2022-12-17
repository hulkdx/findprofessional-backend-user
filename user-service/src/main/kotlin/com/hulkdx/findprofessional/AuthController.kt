package com.hulkdx.findprofessional

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
    suspend fun register(@RequestBody body: User): ResponseEntity<Void> {
        if (!isEmailValid(body.email)) {
            return HttpStatus.BAD_REQUEST.toResponseEntity()
        }
        return try {
            userRepository.save(body)
            HttpStatus.CREATED.toResponseEntity()
        } catch (e: DataIntegrityViolationException) {
            HttpStatus.CONFLICT.toResponseEntity()
        }
    }

    fun isEmailValid(email: String): Boolean {
        // https://emailregex.com/
        return "(?:[a-z0-9!#\$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#\$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])"
            .toRegex()
            .matches(email)
    }
}

interface UserRepository : CoroutineCrudRepository<User, Int>

@Table("users")
data class User(
    val email: String,
    val password: String,
    @Id val id: Int? = null,
)
