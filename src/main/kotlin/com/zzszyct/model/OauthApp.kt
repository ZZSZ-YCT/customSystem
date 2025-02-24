package com.zzszyct.model

/**
 * OAuth 应用模型，用于存储 OAuth 应用的相关信息。
 *
 * @param id 数据库自增ID
 * @param appName 应用名称
 * @param developer 应用开发者
 * @param callbackUrl OAuth 回调地址（标准 OAuth 回调）
 * @param createdAt 应用创建时间的时间戳
 */
data class OauthApp(
    val id: Int? = null,
    val appName: String,
    val developer: String,
    val callbackUrl: String,
    val createdAt: Long
)
