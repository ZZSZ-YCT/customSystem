package com.zzszyct.service

import com.zzszyct.model.User
import com.zzszyct.repository.UserRepository
import com.zzszyct.util.Argon2Util
import com.zzszyct.util.TOTPUtil
import io.ktor.application.ApplicationCall
import org.slf4j.LoggerFactory
import java.util.*

object UserService {
    private val logger = LoggerFactory.getLogger("UserService")

    // 应用启动时调用，用于自动创建 guest 与超级管理员（admin）账户
    fun initializeUsers() {
        // 如果 guest 用户不存在，则创建 guest 用户
        if (UserRepository.getUserByUsername("guest") == null) {
            val guestUser = User(
                username = "guest",
                nickname = "Guest",
                perm = listOf(),
                totp = "", // guest 用户无需 TOTP
                password = "", // guest 用户无需密码
                identity = "guest"
            )
            UserRepository.createUser(guestUser)
            logger.info("Guest user created.")
        }

        // 如果超级管理员（admin）不存在，则创建 admin 用户
        if (UserRepository.getUserByUsername("admin") == null) {
            val randomPassword = generateRandomPassword(16)
            val hashedPassword = Argon2Util.hash(randomPassword)
            val totpSecret = TOTPUtil.generateSecret()
            val adminUser = User(
                username = "admin",
                nickname = "Super Administrator",
                perm = listOf("addNewAdmin"),
                totp = totpSecret,
                password = hashedPassword,
                identity = "superAdmin"
            )
            UserRepository.createUser(adminUser)
            logger.info("SuperAdmin user created. Username: 'admin', Password: $randomPassword, TOTP Secret: $totpSecret")
        }
    }

    // 用户注册接口（管理员注册或自助注册逻辑可在此扩展）
    fun register(registerRequest: RegisterRequest, call: ApplicationCall): ServiceResult {
        // 检查用户名是否已存在
        if (UserRepository.getUserByUsername(registerRequest.username) != null) {
            return ServiceResult(false, "Username already exists", "400")
        }
        // 对密码进行 Argon2 哈希
        val hashedPassword = Argon2Util.hash(registerRequest.password)
        // 生成 TOTP 秘钥
        val totpSecret = TOTPUtil.generateSecret()
        val newUser = User(
            username = registerRequest.username,
            nickname = registerRequest.nickname,
            perm = listOf("user"), // 默认普通用户权限
            totp = totpSecret,
            password = hashedPassword,
            identity = "user"
        )
        val success = UserRepository.createUser(newUser)
        return if (success) ServiceResult(true, "Registration successful. TOTP secret: $totpSecret", "200")
        else ServiceResult(false, "Registration failed", "500")
    }

    // 退出登录（通过使对应 refreshToken 失效）
    fun logout(refreshToken: String): Boolean {
        return TokenService.invalidateRefreshToken(refreshToken)
    }

    // 修改用户权限，管理员（superAdmin 或具备特定权限的 admin）才能操作
    fun modifyPermission(permissionRequest: PermissionRequest, call: ApplicationCall): ServiceResult {
        // 模拟获取当前操作用户（后续可通过 JWT 等方式获取）
        val operatorUsername = "admin" // 示例：假定当前操作者为 admin
        val operatorUser = UserRepository.getUserByUsername(operatorUsername)
            ?: return ServiceResult(false, "Operator not found", "404")
        // 不允许用户修改自己的权限
        if (operatorUser.username == permissionRequest.userName) {
            return ServiceResult(false, "Cannot modify own permissions", "403")
        }
        // 获取目标用户
        val targetUser = UserRepository.getUserByUsername(permissionRequest.userName)
            ?: return ServiceResult(false, "Target user not found", "404")

        // 根据操作类型判断并修改权限
        val newPerm: List<String> = when (permissionRequest.operation) {
            "add" -> {
                if (operatorUser.identity == "superAdmin" ||
                    (operatorUser.identity == "admin" && operatorUser.perm.contains(permissionRequest.permissionName))) {
                    if (!targetUser.perm.contains(permissionRequest.permissionName))
                        targetUser.perm + permissionRequest.permissionName
                    else targetUser.perm
                } else {
                    return ServiceResult(false, "Insufficient permissions", "403")
                }
            }
            "delete" -> {
                if (operatorUser.identity == "superAdmin") {
                    targetUser.perm.filter { it != permissionRequest.permissionName }
                } else {
                    return ServiceResult(false, "Insufficient permissions", "403")
                }
            }
            else -> return ServiceResult(false, "Invalid operation", "400")
        }
        val updatedUser = targetUser.copy(perm = newPerm)
        return if (UserRepository.updateUser(updatedUser))
            ServiceResult(true, "Permission updated successfully", "200")
        else
            ServiceResult(false, "Failed to update permissions", "500")
    }

    // 辅助：根据用户名获取用户（供其他服务使用）
    fun getUserByUsername(username: String): User? {
        return UserRepository.getUserByUsername(username)
    }

    // 生成指定长度的随机密码字符串
    private fun generateRandomPassword(length: Int): String {
        val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"
        return (1..length).map { chars.random() }.joinToString("")
    }
}

// 请求数据模型（可与 Controller 中复用）
data class RegisterRequest(
    val username: String,
    val nickname: String,
    val password: String,
    val invitationCode: String? = null
)

data class PermissionRequest(
    val userName: String,
    val permissionName: String,
    val operation: String
)

// 统一的服务返回结构
data class ServiceResult(val success: Boolean, val message: String, val code: String)
