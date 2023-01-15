package com.hulkdx.findprofessional

import com.hulkdx.findprofessional.config.RuntimeHints
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.ImportRuntimeHints

@ImportRuntimeHints(RuntimeHints::class)
@SpringBootApplication
class FindProfessionalApplication

fun main(args: Array<String>) {
    runApplication<FindProfessionalApplication>(*args)
}
