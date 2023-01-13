package com.hulkdx.findprofessional

import com.hulkdx.findprofessional.models.TokenResponse
import com.hulkdx.findprofessional.models.User
import kotlinx.coroutines.reactive.awaitFirstOrNull
import org.jetbrains.annotations.TestOnly
import org.springframework.security.oauth2.jwt.BadJwtException
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.jwt.JwtClaimsSet
import org.springframework.security.oauth2.jwt.JwtEncoder
import org.springframework.security.oauth2.jwt.JwtEncoderParameters
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder
import org.springframework.stereotype.Service
import java.time.Clock
import java.time.Instant
import java.time.temporal.ChronoUnit

@Service
class TokenService(
    private val jwtEncoder: JwtEncoder,
    private val jwtDecoder: ReactiveJwtDecoder,
    private val clock: Clock,
) {
    fun createToken(user: User) = TokenResponse(
        accessToken = createAccessToken(user),
        refreshToken = createRefreshToken(user),
    )

    fun createAccessToken(user: User): String {
        return jwt {
            issuer("com.hulkdx.findprofessional")
            issuedAt(Instant.now(clock))
            expiresAt(Instant.now(clock).plus(10, ChronoUnit.MINUTES))
            subject(user.id.toString())
        }
    }

    fun createRefreshToken(user: User): String {
        return jwt {
            issuedAt(Instant.now(clock))
            expiresAt(Instant.now(clock).plus(30, ChronoUnit.DAYS))
            subject(user.id.toString())
        }
    }

    private fun jwt(builder: JwtClaimsSet.Builder.() -> Unit): String {
        val claims = JwtClaimsSet.builder().apply(builder).build()
        return jwtEncoder.encode(JwtEncoderParameters.from(claims)).tokenValue
    }

    suspend fun isTokenValid(token: String): Boolean {
        val jwt = decodeJwt(token)
        return isTokenValid(jwt)
    }

    fun isTokenValid(jwt: Jwt?): Boolean {
        if (jwt == null ||
            jwt.expiresAt == null ||
            jwt.subject == null
        ) {
            return false
        }
        val expiredAt = jwt.expiresAt
        return Instant.now(clock).isBefore(expiredAt)
    }

    @TestOnly
    suspend fun decodeJwt(token: String): Jwt? {
        return try {
            return jwtDecoder.decode(token).awaitFirstOrNull()
        } catch (e: BadJwtException) {
            null
        }
    }
}
