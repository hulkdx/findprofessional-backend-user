package com.hulkdx.findprofessional

import com.hulkdx.findprofessional.models.TokenResponse
import org.springframework.stereotype.Service

@Service
class RefreshService(
    private val tokenService: TokenService,
    private val userRepository: UserRepository,
) {
    suspend fun refreshToken(accessToken: String, refreshToken: String): TokenResponse? {
        val refreshTokenJwt = tokenService.decodeJwt(refreshToken)
        val accessTokenUserId = tokenService.getAccessTokenSubject(accessToken) ?: return null
        val refreshTokenUserId = refreshTokenJwt?.subject ?: return null

        if (!tokenService.isTokenValid(refreshTokenJwt) ||
            accessTokenUserId != refreshTokenUserId
        ) {
            return null
        }

        val user = userRepository.findById(id = refreshTokenUserId.toInt()) ?: return null
        return tokenService.createToken(user)
    }
}
