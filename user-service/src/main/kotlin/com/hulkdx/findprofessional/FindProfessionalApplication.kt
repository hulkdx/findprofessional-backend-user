package com.hulkdx.findprofessional

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

//import org.springframework.web.bind.annotation.GetMapping
//import org.springframework.web.bind.annotation.RestController

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
