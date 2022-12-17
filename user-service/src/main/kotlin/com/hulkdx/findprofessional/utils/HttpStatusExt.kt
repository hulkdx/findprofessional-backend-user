package com.hulkdx.findprofessional.utils

import com.hulkdx.findprofessional.models.ApiError
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity

object R {
    fun created() = ResponseEntity.status(HttpStatus.CREATED).build<Unit>()
    fun conflict() = ResponseEntity.status(HttpStatus.CONFLICT).build<Unit>()
    fun badRequest(message: String) = ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiError(message))
}
