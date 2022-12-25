package com.hulkdx.findprofessional

import com.hulkdx.findprofessional.models.User
import kotlinx.coroutines.reactive.awaitFirstOrNull
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.oauth2.jwt.BadJwtException
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
        val userId = passwordEncoder.encode("${user.id}")
        val email = passwordEncoder.encode(user.email)

        val claims = JwtClaimsSet.builder()
            .issuer("com.hulkdx.findprofessional")
            .issuedAt(Instant.now())
            .expiresAt(Instant.now().plus(30, ChronoUnit.DAYS))
            .subject("${userId},${email}")
            .claim("roles", "normal")
            .build()
        return jwtEncoder.encode(JwtEncoderParameters.from(claims)).tokenValue
    }

    suspend fun isTokenValid(token: String): Boolean {
        val claims = try {
            jwtDecoder.decode(token).awaitFirstOrNull()
        } catch (e: BadJwtException) {
            return false
        }
        return true
    }
}
