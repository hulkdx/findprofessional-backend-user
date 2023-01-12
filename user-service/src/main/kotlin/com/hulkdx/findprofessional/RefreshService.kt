package com.hulkdx.findprofessional

import org.springframework.stereotype.Service

@Service
class RefreshService(
    private val tokenService: TokenService,
) {
    suspend fun refreshToken(accessToken: String, refreshToken: String): Any? {
        val accessTokenJwt = tokenService.decodeJwt(accessToken)
        val refreshTokenJwt = tokenService.decodeJwt(refreshToken)
        if (!tokenService.isTokenValid(accessTokenJwt)) {
            return null
        }
        if (!tokenService.isTokenValid(refreshTokenJwt)) {
            return null
        }
        // TODO: check if the user of accessToken is the same as refreshToken
        // TODO: if refreshToken is invalid unauth, logout the app

        // TODO: generate a new accessToken
        // TODO: generate a new refreshToken

        TODO("Not yet implemented")
    }
}
