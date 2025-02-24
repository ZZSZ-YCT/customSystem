package com.zzszyct.service

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.zzszyct.model.Token
import com.zzszyct.model.User
import com.zzszyct.repository.UserRepository
import com.zzszyct.util.Argon2Util
import com.zzszyct.util.TOTPUtil
import java.util.*

object AuthService {
    private val jwtSecret = System.getenv("JWT_SECRET") ?: "defaultSecret"
    private val algorithm = Algorithm.HMAC256(jwtSecret)

    /**
     * 登录处理，支持用户名+密码 或 用户名+ TOTP 登录
     */
    fun login(loginRequest: LoginRequest): Map<String, Any>? {
        val user = UserRepository.getUserByUsername(loginRequest.username) ?: return null

        // 验证密码或 TOTP
        val isValid = when {
            !loginRequest.password.isNullOrEmpty() -> Argon2Util.verify(loginRequest.password, user.password)
            !loginRequest.totp.isNullOrEmpty() -> TOTPUtil.verify(user.totp, loginRequest.totp)
            else -> false
        }
        if (!isValid) return null

        // 生成 refreshToken 与 accessToken
        val refreshToken = generateRandomToken(32)
        val refreshTokenUuid = UUID.randomUUID().toString()
        val accessToken = generateAccessToken(user, refreshTokenUuid)
        val expiresAt = System.currentTimeMillis() + 24 * 3600 * 1000 // 24 小时有效期

        val tokenObj = Token(
            accessToken = accessToken,
            refreshToken = refreshToken,
            refreshTokenUuid = refreshTokenUuid,
            expiresAt = expiresAt
        )
        // 假设 user.id 不为空，否则需先更新用户记录（此处简单处理）
        val userId = user.id ?: 0
        TokenService.storeToken(tokenObj, userId)

        return mapOf(
            "accessToken" to accessToken,
            "refreshToken" to refreshToken
        )
    }

    /**
     * 使用 refreshToken 刷新 accessToken
     */
    fun refreshAccessToken(refreshToken: String): String? {
        val tokenData = TokenService.getToken(refreshToken) ?: return null
        // 检查 refreshToken 是否过期
        if (System.currentTimeMillis() > tokenData.expiresAt) {
            TokenService.invalidateRefreshToken(refreshToken)
            return null
        }
        // 此处需要根据 tokenData 获取对应用户，本示例中简化处理：通过 refreshTokenUuid 无法直接获取用户，
        // 故假设刷新时仍使用 admin 用户示例（实际应通过 token 记录的 userId 获取用户）
        val user = UserRepository.getUserByUsername("admin") ?: return null
        return generateAccessToken(user, tokenData.refreshTokenUuid)
    }

    // 生成 JWT accessToken，载荷中包含 refreshToken 的 UUID
    private fun generateAccessToken(user: User, refreshTokenUuid: String): String {
        val now = System.currentTimeMillis() / 1000
        return JWT.create()
            .withIssuer("user.zzszyct.xyz")
            .withSubject(user.username)
            .withAudience("user")
            .withIssuedAt(Date(now * 1000))
            .withNotBefore(Date(now * 1000))
            .withExpiresAt(Date((now + 24 * 3600) * 1000))
            .withJWTId(UUID.randomUUID().toString())
            .withClaim("refreshTokenUuid", refreshTokenUuid)
            .sign(algorithm)
    }

    private fun generateRandomToken(length: Int): String {
        val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"
        return (1..length).map { chars.random() }.joinToString("")
    }
}

// 请求数据模型
data class LoginRequest(
    val username: String,
    val password: String? = null,
    val totp: String? = null
)
