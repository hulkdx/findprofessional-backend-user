package com.hulkdx.findprofessional

import com.hulkdx.findprofessional.models.User
import kotlinx.coroutines.reactive.awaitFirstOrNull
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.oauth2.jwt.BadJwtException
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.jwt.JwtClaimsSet
import org.springframework.security.oauth2.jwt.JwtEncoder
import org.springframework.security.oauth2.jwt.JwtEncoderParameters
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder
import org.springframework.stereotype.Service
import java.time.Instant
import java.time.temporal.ChronoUnit

@Service
class AuthTokenService(
    private val passwordEncoder: PasswordEncoder,
    private val jwtEncoder: JwtEncoder,
    private val jwtDecoder: ReactiveJwtDecoder,
) {
    fun createToken(user: User): String {
        val email = passwordEncoder.encode(user.email)

        val claims = JwtClaimsSet.builder()
            .issuer("com.hulkdx.findprofessional")
            .issuedAt(Instant.now())
            .expiresAt(Instant.now().plus(30, ChronoUnit.DAYS))
            .subject(email)
            .claim("roles", "normal")
            .build()
        return jwtEncoder.encode(JwtEncoderParameters.from(claims)).tokenValue
    }

    suspend fun isTokenValid(token: String): Boolean {
        val jwt = decodeJwt(token) ?: return false
        return true
    }

    suspend fun decodeJwt(token: String): Jwt? {
        return try {
            jwtDecoder.decode(token).awaitFirstOrNull()
        } catch (e: BadJwtException) {
            null
        }
    }
}
