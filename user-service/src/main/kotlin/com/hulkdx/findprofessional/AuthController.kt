package com.hulkdx.findprofessional

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

/*


@RestController
@RequestMapping("/auth")
@EnableR2dbcAuditing
class AuthController(
    private val userRepository: UserRepository,
) {

    private val passwordEncoder: BCryptPasswordEncoder = BCryptPasswordEncoder()

    @PostMapping("/register")
    suspend fun register(@RequestBody body: RegisterRequest): ResponseEntity<*> {
        if (!Validator.isEmailValid(body.email)) {
            return R.badRequest("Email not valid")
        }
        if (!Validator.isPasswordValid(body.password)) {
            return R.badRequest("Password not valid")
        }
        return try {
            val password = passwordEncoder.encode(body.password)
            val user = User(body.email, password)
            userRepository.save(user)
            R.created()
        } catch (e: DataIntegrityViolationException) {
            R.conflict()
        }
    }
}
*/

@RestController
class Controller {
    @GetMapping("/")
    fun hello() = "hello world"
}
