package com.zzszyct.controller

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import com.zzszyct.service.OauthService

/**
 * OAuth 相关接口：
 * - POST /app/creation：由 superAdmin 创建 OAuth 应用
 * - GET /oauth/callback：处理 OAuth 回调，返回 accessToken 与 refreshToken
 */
fun Route.oauthRoutes() {
    // 创建 OAuth 应用，仅 superAdmin 有权限
    post("/app/creation") {
        val oauthRequest = call.receive<OauthCreationRequest>()
        val oauthApp = OauthService.createOauthApp(oauthRequest, call)
        if (oauthApp != null) {
            call.respond(HttpStatusCode.OK, oauthApp)
        } else {
            call.respond(HttpStatusCode.Forbidden, mapOf("error" to "Insufficient permissions or invalid data"))
        }
    }

    // OAuth 回调处理（示例：通过 GET 请求接收 code 参数）
    get("/oauth/callback") {
        val code = call.request.queryParameters["code"]
        if (code.isNullOrEmpty()) {
            call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Missing 'code' parameter"))
            return@get
        }
        val tokenResponse = OauthService.handleCallback(code)
        if (tokenResponse != null) {
            call.respond(HttpStatusCode.OK, tokenResponse)
        } else {
            call.respond(HttpStatusCode.Unauthorized, mapOf("error" to "OAuth callback processing failed"))
        }
    }
}

// 请求数据模型

data class OauthCreationRequest(
    val appName: String,
    val developer: String,
    val callbackUrl: String
)
