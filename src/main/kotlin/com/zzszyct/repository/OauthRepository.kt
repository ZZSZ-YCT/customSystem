package com.zzszyct.repository

import com.zzszyct.model.OauthApp
import java.sql.Connection
import java.sql.DriverManager
import java.sql.Statement

object OauthRepository {
    private val connection: Connection by lazy {
        val dbPath = System.getenv("DB_PATH") ?: "/data/user.db"
        DriverManager.getConnection("jdbc:sqlite:$dbPath")
    }

    // 创建 OAuth 应用记录，并返回带自增 id 的对象
    fun createOauthApp(oauthApp: OauthApp): OauthApp? {
        val sql = "INSERT INTO oauth_apps (app_name, developer, callback_url, created_at) VALUES (?, ?, ?, ?)"
        val ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)
        ps.setString(1, oauthApp.appName)
        ps.setString(2, oauthApp.developer)
        ps.setString(3, oauthApp.callbackUrl)
        ps.setLong(4, oauthApp.createdAt)
        val result = ps.executeUpdate()
        if (result > 0) {
            val rs = ps.generatedKeys
            if (rs.next()) {
                val id = rs.getInt(1)
                return oauthApp.copy(id = id)
            }
        }
        return null
    }
}
