package com.zzszyct.model

/**
 * 用户模型，所有用户数据以 JSON 格式保存。
 *
 * @param id 可选的用户ID（数据库生成）
 * @param username 用户名，唯一标识
 * @param nickname 用户昵称
 * @param perm 用户权限数组，如 ["register", "invitation", ...]
 * @param totp TOTP 秘钥（Base32 编码）
 * @param password 经过 Argon2 哈希后的密码
 * @param identity 用户身份，取值范围："superAdmin", "admin", "user", "guest"
 */
data class User(
    val id: Int? = null,
    val username: String,
    val nickname: String,
    val perm: List<String> = listOf(),
    val totp: String,
    val password: String,
    val identity: String
)
