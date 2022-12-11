package com.hulkdx.findprofessional

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.annotation.Id
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController


@SpringBootApplication
class FindProfessionalApplication

fun main(args: Array<String>) {
	runApplication<FindProfessionalApplication>(*args)
}

@RestController
class HelloController {
	@GetMapping("/")
	fun index(): String {
		return "Hello World!"
	}
}

interface CustomerRepository: CoroutineCrudRepository<Customer, Int>

data class Customer(@Id val id: Int, val name: String)
