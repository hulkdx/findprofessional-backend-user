package com.hulkdx.findprofessional

import org.springframework.stereotype.Service

@Service
class RefreshService(
    private val tokenService: TokenService,
    private val userRepository: UserRepository,
) {
    suspend fun refreshToken(accessToken: String, refreshToken: String): Any? {
        val accessTokenJwt = tokenService.decodeJwt(accessToken)
        val refreshTokenJwt = tokenService.decodeJwt(refreshToken)
        val accessTokenUserId = accessTokenJwt?.subject ?: return null
        val refreshTokenUserId = refreshTokenJwt?.subject ?: return null

        if (!tokenService.isTokenValid(refreshTokenJwt) ||
            accessTokenUserId != refreshTokenUserId
        ) {
            return null
        }
        val user = userRepository.findById(id = refreshTokenUserId.toInt()) ?: return null
        // TODO: generate a new accessToken
        // TODO: generate a new refreshToken

        TODO("Not yet implemented")
    }
}
