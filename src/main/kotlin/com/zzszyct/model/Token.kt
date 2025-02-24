package com.zzszyct.model

/**
 * Token 数据模型，用于存储 accessToken 与 refreshToken 相关信息。
 *
 * @param accessToken JWT 令牌，有效期 24 小时，载荷中包含 refreshToken 的 UUID
 * @param refreshToken 32 字符长的令牌，有效期 30 天
 * @param refreshTokenUuid 用于在 JWT payload 中记录的 refreshToken 的 UUID
 * @param expiresAt 令牌过期时间（时间戳，单位：秒或毫秒，按实际需求设定）
 */
data class Token(
    val accessToken: String,
    val refreshToken: String,
    val refreshTokenUuid: String,
    val expiresAt: Long
)
