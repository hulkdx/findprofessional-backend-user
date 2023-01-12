package com.hulkdx.findprofessional

import org.springframework.stereotype.Service

@Service
class RefreshService(
    private val tokenService: TokenService,
) {
    suspend fun refreshToken(accessToken: String, refreshToken: String): Any? {
        if (!tokenService.isTokenValid(accessToken)) {
            return null
        }
        // TODO: check if refreshToken is valid
        // TODO: check if the user of accessToken is the same as refreshToken
        // TODO: if refreshToken is invalid unauth, logout the app

        // TODO: generate a new accessToken
        // TODO: generate a new refreshToken

        TODO("Not yet implemented")
    }
}
