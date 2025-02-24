# customSystem

# 英才二班产业服务站后端

该项目作为 **user.zzszyct.xyz** 的后端，基于 Kotlin 与 Ktor 构建，提供 OAuth2、JWT 认证、用户注册/登录及权限管理等功能。

## 特性

- 基于 Kotlin + Ktor 后端框架
- 使用 SQLite 存储数据（数据库文件挂载到宿主机）
- JWT (HS256) 认证，秘钥通过 Docker 环境变量传入
- 支持 Oauth2 登录（仅 superAdmin 可创建 OAuth 应用）
- 密码采用 Argon2 哈希，登录支持 TOTP（二次验证）
- Docker Compose 管理部署，环境配置文件为 `.env`（参照 `.env.example`）

## 构建与运行

1. **复制环境文件**  
将 `.env.example` 复制为 `.env` 并根据需求修改配置：
```bash
cp .env.example .env
```
2. **使用 Gradle 构建项目**
```
./gradlew build
```
3. **使用 Docker Compose 启动项目**
```
docker-compose up --build
```
项目默认监听端口为 8080，详情请参阅配置文件。