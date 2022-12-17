package com.hulkdx.findprofessional.utils

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity

fun <T> HttpStatus.toResponseEntity(): ResponseEntity<T> {
    return ResponseEntity.status(this).build()
}
