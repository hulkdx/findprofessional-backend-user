package com.hulkdx.findprofessional.utils

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity

fun HttpStatus.toResponseEntity(): ResponseEntity<Unit> {
    return ResponseEntity.status(this).build()
}

fun <T> HttpStatus.toResponseEntity(body: T): ResponseEntity<T> {
    return ResponseEntity.status(this).body(body)
}
