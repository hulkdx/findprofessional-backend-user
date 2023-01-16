package com.hulkdx.findprofessional.utils

import org.springframework.test.web.reactive.server.WebTestClient
import reactor.core.publisher.Mono

inline fun <reified T : Any> WebTestClient.RequestBodySpec.body(body: T) =
    body(Mono.just(body), T::class.java)
        .exchange()
        .expectStatus()

fun <T> WebTestClient.ResponseSpec.response(clazz: Class<T>): T =
    returnResult(clazz)
        .responseBody
        .blockFirst()!!
