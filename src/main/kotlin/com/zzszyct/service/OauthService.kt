package com.zzszyct.service

import com.zzszyct.model.OauthApp
import com.zzszyct.repository.OauthRepository
import io.ktor.application.ApplicationCall
import org.slf4j.LoggerFactory

object OauthService {
    private val logger = LoggerFactory.getLogger("OauthService")

    /**
     * 仅允许 superAdmin（admin）创建 OAuth 应用
     */
    fun createOauthApp(oauthRequest: OauthCreationRequest, call: ApplicationCall): OauthApp? {
        // 模拟从调用上下文中获取当前用户，实际需通过认证信息判断
        val operatorUsername = "admin" // 示例：假定当前操作者为 admin
        val user = UserService.getUserByUsername(operatorUsername)
        if (user == null || user.identity != "superAdmin") {
            return null
        }
        val newApp = OauthApp(
            appName = oauthRequest.appName,
            developer = oauthRequest.developer,
            callbackUrl = oauthRequest.callbackUrl,
            createdAt = System.currentTimeMillis()
        )
        return OauthRepository.createOauthApp(newApp)
    }

    /**
     * 处理 OAuth 回调，示例中直接返回 token 信息（实际流程可能涉及第三方服务）
     */
    fun handleCallback(code: String): Map<String, String>? {
        // 模拟回调处理流程，此处直接以 admin 用户生成 token 返回
        val dummyUser = UserService.getUserByUsername("admin") ?: return null
        val tokenResponse = AuthService.login(LoginRequest(username = dummyUser.username, password = null, totp = null))
        return tokenResponse
    }
}

// 请求数据模型，用于 OAuth 应用创建
data class OauthCreationRequest(
    val appName: String,
    val developer: String,
    val callbackUrl: String
)
