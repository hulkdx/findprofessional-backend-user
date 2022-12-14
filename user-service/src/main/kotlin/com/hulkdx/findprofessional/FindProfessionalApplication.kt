package com.hulkdx.findprofessional

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import org.springframework.data.repository.kotlin.CoroutineCrudRepository


@SpringBootApplication
class FindProfessionalApplication

fun main(args: Array<String>) {
    runApplication<FindProfessionalApplication>(*args)
}

interface CustomerRepository : CoroutineCrudRepository<User, Int>

@Table("users")
data class User(@Id val id: Int, val name: String)
