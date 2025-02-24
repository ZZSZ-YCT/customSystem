package com.zzszyct

import io.ktor.application.*
import io.ktor.features.ContentNegotiation
import io.ktor.jackson.jackson
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.features.StatusPages
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import org.slf4j.LoggerFactory
import com.zzszyct.config.AppConfig

/**
 * 项目启动入口，读取配置、初始化服务、注册路由、设置全局异常处理等。
 */
fun main(args: Array<String>) {
    // 从环境变量加载配置
    val config = AppConfig.load()

    // 启动 Ktor 服务，监听配置中指定的端口
    embeddedServer(Netty, port = config.appPort, module = Application::module).start(wait = true)
}

/**
 * Ktor 应用模块，用于配置各项功能及路由。
 */
fun Application.module() {
    val logger = LoggerFactory.getLogger("Application")
    val config = AppConfig.load()  // 获取应用配置

    // 安装 ContentNegotiation，并使用 Jackson 进行 JSON 序列化
    install(ContentNegotiation) {
        jackson {
            // 可在此配置 Jackson 的 ObjectMapper，如日期格式、序列化策略等
        }
    }

    // 全局异常处理，捕获未处理异常并返回 500 错误
    install(StatusPages) {
        exception<Throwable> { cause ->
            logger.error("Unhandled exception", cause)
            call.respond(
                HttpStatusCode.InternalServerError,
                mapOf("error" to (cause.message ?: "Unknown error"))
            )
        }
    }

    // 配置基础路由
    routing {
        // 根路径，返回欢迎信息
        get("/") {
            call.respondText("Welcome to user.zzszyct.xyz!", ContentType.Text.Plain)
        }

        // 后续可在此添加更多的路由配置，例如用户登录、注册、OAuth 接入等接口
    }

    // 在这里可进行数据库初始化、服务初始化等操作，
    // 例如调用 UserService.initialize(config) 来自动创建 guest 与 admin 用户
    logger.info("Application started on port ${config.appPort} with DB path ${config.dbPath}")
}
