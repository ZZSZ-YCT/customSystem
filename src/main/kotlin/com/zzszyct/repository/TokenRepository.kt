package com.zzszyct.repository

import com.zzszyct.model.Token
import java.sql.Connection
import java.sql.DriverManager

object TokenRepository {
    private val connection: Connection by lazy {
        val dbPath = System.getenv("DB_PATH") ?: "/data/user.db"
        DriverManager.getConnection("jdbc:sqlite:$dbPath")
    }

    // 存储 refreshToken 及其关联的用户信息
    fun createToken(token: Token, userId: Int): Boolean {
        val sql = "INSERT INTO tokens (token_uuid, refresh_token, user_id, expiry) VALUES (?, ?, ?, ?)"
        val ps = connection.prepareStatement(sql)
        ps.setString(1, token.refreshTokenUuid)
        ps.setString(2, token.refreshToken)
        ps.setInt(3, userId)
        ps.setLong(4, token.expiresAt)
        return ps.executeUpdate() > 0
    }

    // 根据 refreshToken 查找对应的 Token 记录
    fun getTokenByRefreshToken(refreshToken: String): Token? {
        val sql = "SELECT token_uuid, refresh_token, expiry FROM tokens WHERE refresh_token = ?"
        val ps = connection.prepareStatement(sql)
        ps.setString(1, refreshToken)
        val rs = ps.executeQuery()
        return if (rs.next()) {
            Token(
                accessToken = "", // accessToken 不存储，仅由 JWT 动态生成
                refreshToken = rs.getString("refresh_token"),
                refreshTokenUuid = rs.getString("token_uuid"),
                expiresAt = rs.getLong("expiry")
            )
        } else null
    }

    // 使 refreshToken 失效（删除记录）
    fun invalidateToken(refreshToken: String): Boolean {
        val sql = "DELETE FROM tokens WHERE refresh_token = ?"
        val ps = connection.prepareStatement(sql)
        ps.setString(1, refreshToken)
        return ps.executeUpdate() > 0
    }
}
