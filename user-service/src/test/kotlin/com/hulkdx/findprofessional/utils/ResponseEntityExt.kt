package com.hulkdx.findprofessional.utils

import com.hulkdx.findprofessional.models.ApiError
import org.springframework.http.ResponseEntity


val <T> ResponseEntity<T>.errorMessage: String
    get() = (body as? ApiError)?.message ?: ""
