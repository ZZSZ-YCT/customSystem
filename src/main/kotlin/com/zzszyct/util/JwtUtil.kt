package com.zzszyct.util

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.interfaces.DecodedJWT
import java.util.*

/**
 * JwtUtil 用于生成和解析 JWT 令牌，采用 HS256 算法，
 * 载荷包含标准字段：iss、sub、aud、exp、nbf、iat、jti，
 * 以及自定义字段 refreshTokenUuid。
 */
object JwtUtil {
    /**
     * 生成 JWT 令牌
     *
     * @param issuer 签发者
     * @param subject 主题（例如用户名）
     * @param audience 接收者
     * @param expiresAt 过期时间
     * @param notBefore 生效时间
     * @param issuedAt 签发时间
     * @param jwtId JWT 标识符
     * @param refreshTokenUuid 与此 JWT 关联的 refreshToken UUID
     * @param secret JWT 签名密钥
     * @return 生成的 JWT 字符串
     */
    fun generateToken(
        issuer: String,
        subject: String,
        audience: String,
        expiresAt: Date,
        notBefore: Date,
        issuedAt: Date,
        jwtId: String,
        refreshTokenUuid: String,
        secret: String
    ): String {
        val algorithm = Algorithm.HMAC256(secret)
        return JWT.create()
            .withIssuer(issuer)
            .withSubject(subject)
            .withAudience(audience)
            .withExpiresAt(expiresAt)
            .withNotBefore(notBefore)
            .withIssuedAt(issuedAt)
            .withJWTId(jwtId)
            .withClaim("refreshTokenUuid", refreshTokenUuid)
            .sign(algorithm)
    }

    /**
     * 验证并解析 JWT 令牌
     *
     * @param token 待验证的 JWT 字符串
     * @param secret JWT 签名密钥
     * @return 解析后的 DecodedJWT 对象，如果验证失败则返回 null
     */
    fun verifyToken(token: String, secret: String): DecodedJWT? {
        return try {
            val algorithm = Algorithm.HMAC256(secret)
            val verifier = JWT.require(algorithm)
                .build()
            verifier.verify(token)
        } catch (ex: Exception) {
            null
        }
    }
}
