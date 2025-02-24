package com.zzszyct.repository

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.zzszyct.model.User
import java.sql.Connection
import java.sql.DriverManager

object UserRepository {
    private val objectMapper = jacksonObjectMapper()
    private val connection: Connection by lazy {
        // 从环境变量中获取 SQLite 数据库文件挂载路径
        val dbPath = System.getenv("DB_PATH") ?: "/data/user.db"
        DriverManager.getConnection("jdbc:sqlite:$dbPath")
    }

    // 创建用户表中的记录（用户数据以 JSON 格式保存在 data 字段）
    fun createUser(user: User): Boolean {
        val sql = "INSERT INTO users (username, data) VALUES (?, ?)"
        val ps = connection.prepareStatement(sql)
        ps.setString(1, user.username)
        ps.setString(2, objectMapper.writeValueAsString(user))
        return ps.executeUpdate() > 0
    }

    // 根据用户名获取用户数据
    fun getUserByUsername(username: String): User? {
        val sql = "SELECT data FROM users WHERE username = ?"
        val ps = connection.prepareStatement(sql)
        ps.setString(1, username)
        val rs = ps.executeQuery()
        return if (rs.next()) {
            val json = rs.getString("data")
            objectMapper.readValue(json, User::class.java)
        } else null
    }

    // 更新用户数据（将整个 JSON 数据写回）
    fun updateUser(user: User): Boolean {
        val sql = "UPDATE users SET data = ? WHERE username = ?"
        val ps = connection.prepareStatement(sql)
        ps.setString(1, objectMapper.writeValueAsString(user))
        ps.setString(2, user.username)
        return ps.executeUpdate() > 0
    }
}
