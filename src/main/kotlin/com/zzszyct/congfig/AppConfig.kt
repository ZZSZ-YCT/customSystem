package com.zzszyct.config

/**
 * AppConfig 用于保存并加载应用的配置项，这些配置项通过环境变量进行设置。
 *
 * @property jwtSecret JWT 签名密钥（HS256），必须设置
 * @property dbPath SQLite 数据库文件的存储路径，默认值为 /data/user.db
 * @property appPort 应用监听端口，默认值为 8080
 */
data class AppConfig(
    val jwtSecret: String,
    val dbPath: String,
    val appPort: Int
) {
    companion object {
        /**
         * 从环境变量中加载配置，如果缺少必要的变量则抛出异常。
         */
        fun load(): AppConfig {
            val jwtSecret = System.getenv("JWT_SECRET")
                ?: throw IllegalStateException("JWT_SECRET must be set")
            val dbPath = System.getenv("DB_PATH") ?: "/data/user.db"
            val appPortStr = System.getenv("APP_PORT") ?: "8080"
            val appPort = appPortStr.toIntOrNull() ?: 8080

            return AppConfig(
                jwtSecret = jwtSecret,
                dbPath = dbPath,
                appPort = appPort
            )
        }
    }
}
