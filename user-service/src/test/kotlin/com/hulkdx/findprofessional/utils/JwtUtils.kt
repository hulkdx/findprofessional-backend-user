package com.hulkdx.findprofessional.utils

import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.jwt.JwtClaimNames
import java.time.Instant


fun createJwt(
    expiredAt: Instant? = Instant.now().plusSeconds(10),
    issuedAt: Instant = Instant.now(),
    subject: String? = "subject",
) = Jwt(
    "123",
    issuedAt,
    expiredAt,
    mapOf("" to ""),
    mapOf(
        JwtClaimNames.SUB to subject,
    ),
)
