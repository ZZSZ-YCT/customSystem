package com.zzszyct.service

import com.zzszyct.model.Token
import com.zzszyct.repository.TokenRepository

object TokenService {
    fun storeToken(token: Token, userId: Int): Boolean {
        return TokenRepository.createToken(token, userId)
    }

    fun getToken(refreshToken: String): Token? {
        return TokenRepository.getTokenByRefreshToken(refreshToken)
    }

    fun invalidateRefreshToken(refreshToken: String): Boolean {
        return TokenRepository.invalidateToken(refreshToken)
    }
}
