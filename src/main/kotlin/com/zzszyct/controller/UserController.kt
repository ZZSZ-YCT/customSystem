package com.zzszyct.controller

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import com.zzszyct.service.AuthService
import com.zzszyct.service.UserService

/**
 * 用户相关接口，包含登录、注册、退出、刷新令牌以及权限管理等操作。
 */
fun Route.userRoutes() {
    // POST /user/login：支持用户名+密码 或 用户名+TOTP 登录
    post("/user/login") {
        val loginRequest = call.receive<LoginRequest>()
        val tokenResponse = AuthService.login(loginRequest)
        if (tokenResponse != null) {
            call.respond(HttpStatusCode.OK, tokenResponse)
        } else {
            call.respond(HttpStatusCode.Unauthorized, mapOf("error" to "Invalid credentials"))
        }
    }

    // POST /user/register：注册接口，区分管理员注册与自助注册（依赖 guest 用户权限 "register"）
    post("/user/register") {
        val registerRequest = call.receive<RegisterRequest>()
        val result = UserService.register(registerRequest, call)
        if (result.success) {
            call.respond(HttpStatusCode.OK, result)
        } else {
            // 若 guest 用户不允许注册，则返回 403
            if (result.code == "403") {
                call.respond(HttpStatusCode.Forbidden, result)
            } else {
                call.respond(HttpStatusCode.BadRequest, result)
            }
        }
    }

    // POST /user/logout：退出登录，传入 refreshToken 后使该 refreshToken 及其签发的所有 accessToken 失效
    post("/user/logout") {
        val logoutRequest = call.receive<LogoutRequest>()
        val result = UserService.logout(logoutRequest.refreshToken)
        if (result) {
            call.respond(HttpStatusCode.OK, mapOf("message" to "Logout successful"))
        } else {
            call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Logout failed"))
        }
    }

    // POST /user/getAccessToken：使用 refreshToken 获取新的 accessToken
    post("/user/getAccessToken") {
        val refreshRequest = call.receive<RefreshTokenRequest>()
        val newAccessToken = AuthService.refreshAccessToken(refreshRequest.refreshToken)
        if (newAccessToken != null) {
            call.respond(HttpStatusCode.OK, mapOf("accessToken" to newAccessToken))
        } else {
            call.respond(HttpStatusCode.Unauthorized, mapOf("error" to "Invalid or expired refresh token"))
        }
    }

    // POST /user/prem：修改用户权限（添加或删除），操作仅允许特定身份执行
    post("/user/prem") {
        val permissionRequest = call.receive<PermissionRequest>()
        val result = UserService.modifyPermission(permissionRequest, call)
        if (result.success) {
            call.respond(HttpStatusCode.OK, mapOf("message" to "Permission updated"))
        } else {
            call.respond(HttpStatusCode.Forbidden, result)
        }
    }
}

// 请求数据模型

data class LoginRequest(
    val username: String,
    val password: String? = null,
    val totp: String? = null
)

data class RegisterRequest(
    val username: String,
    val nickname: String,
    val password: String,
    // 邀请码字段必须存在，若无邀请码则传 null 或空字符串
    val invitationCode: String? = null
)

data class LogoutRequest(
    val refreshToken: String
)

data class RefreshTokenRequest(
    val refreshToken: String
)

data class PermissionRequest(
    val userName: String,
    val permissionName: String,
    // 操作类型：仅允许 "add" 或 "delete"
    val operation: String
)
