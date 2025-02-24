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

## 项目架构

```
/英才二班产业服务站后端/  
├── README.md
├── build.gradle.kts                // 或 pom.xml，根据使用的构建工具
├── Dockerfile                      // 构建后端镜像
├── docker-compose.yml              // 包含环境变量加载(.env)及卷挂载配置
├── .env.example                    // 系统默认环境变量示例文件
├── src
│   ├── main
│   │   ├── kotlin
│   │   │   └── com
│   │   │       └── zzszyct
│   │   │           ├── Application.kt        // 项目启动入口，配置服务、路由等
│   │   │           ├── config
│   │   │           │   └── AppConfig.kt        // 读取环境变量、加载配置（包括JWT秘钥等）
│   │   │           ├── controller
│   │   │           │   ├── UserController.kt     // 包含 /user/login、/user/register、/user/logout、/user/getAccessToken、/user/prem 等接口
│   │   │           │   └── OauthController.kt    // 包含 /app/creation 接口及 OAuth 回调处理
│   │   │           ├── model
│   │   │           │   ├── User.kt               // 用户模型，内部数据以 JSON 格式保存（包括 nickname、perm、totp、password、identity 等字段）
│   │   │           │   ├── Token.kt              // accessToken/refreshToken 数据模型（注意 payload 中需要记录 refreshToken 的 UUID）
│   │   │           │   └── OauthApp.kt           // OAuth 应用模型（应用名称、开发者、回调地址）
│   │   │           ├── repository
│   │   │           │   ├── UserRepository.kt     // 用户数据读写（SQLite，挂载卷路径配置）
│   │   │           │   ├── TokenRepository.kt    // refreshToken 与用户关系的持久化管理
│   │   │           │   └── OauthRepository.kt    // OAuth 应用数据管理
│   │   │           ├── service
│   │   │           │   ├── UserService.kt        // 用户注册、登录、权限管理、自动创建 guest 及 superAdmin 用户（启动时随机生成 admin 的 16 位密码及 TOTP 输出日志）
│   │   │           │   ├── AuthService.kt        // 认证服务，包含 JWT 的生成、校验、refreshToken 更新、登出等操作
│   │   │           │   └── OauthService.kt       // OAuth 应用创建及回调处理（仅 superAdmin 有权限创建）
│   │   │           └── util
│   │   │               ├── JwtUtil.kt            // JWT 生成、解析（HS256，载荷包含 iss、sub、aud、exp、nbf、iat、jti 及 refreshToken UUID）
│   │   │               ├── Argon2Util.kt         // 密码哈希与校验（Argon2）
│   │   │               └── TOTPUtil.kt           // TOTP 秘钥生成与验证码验证
│   │   └── resources
│   │       ├── application.conf              // 框架配置（如 Ktor 的配置文件，定义端口、路由、数据库连接等）
│   │       ├── logback.xml                   // 日志配置（输出 admin 账号初始密码及 TOTP 信息）
│   │       └── schema.sql                    // SQLite 数据库初始化脚本（如建表、索引配置）
└── tests
└── kotlin
└── com
└── zzszyct
└── (单元测试、集成测试代码)
```