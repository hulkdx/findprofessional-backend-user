package com.hulkdx.findprofessional.utils

import com.hulkdx.findprofessional.models.ApiError
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity

object R {
    fun <T> ok(body: T) = ResponseEntity.status(HttpStatus.OK)
        .body(body)
    fun created() = ResponseEntity.status(HttpStatus.CREATED).build<Unit>()
    fun conflict() = ResponseEntity.status(HttpStatus.CONFLICT).build<Unit>()
    fun conflict(message: String) = ResponseEntity.status(HttpStatus.CONFLICT).body(ApiError(message))
    fun badRequest(message: String) = ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiError(message))
    fun unauthorized() = ResponseEntity.status(HttpStatus.UNAUTHORIZED).build<Unit>()
}
