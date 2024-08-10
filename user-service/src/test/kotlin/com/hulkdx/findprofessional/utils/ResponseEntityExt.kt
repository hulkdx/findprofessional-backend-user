package com.hulkdx.findprofessional.utils

import com.hulkdx.findprofessional.model.ApiError
import org.springframework.http.ResponseEntity


val <T> ResponseEntity<T>.errorMessage: String
    get() = (body as? ApiError)?.message ?: ""
