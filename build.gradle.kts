plugins {
    kotlin("jvm") version "1.6.21"
    application
}

group = "com.zzszyct"
version = "1.0.0"

application {
    mainClass.set("com.zzszyct.ApplicationKt")
}

repositories {
    mavenCentral()
}

dependencies {
    // Ktor 核心依赖
    implementation("io.ktor:ktor-server-netty:1.6.7")
    implementation("io.ktor:ktor-server-core:1.6.7")
    implementation("io.ktor:ktor-jackson:1.6.7")

    // JWT 支持
    implementation("com.auth0:java-jwt:3.18.1")

    // SQLite JDBC 驱动
    implementation("org.xerial:sqlite-jdbc:3.36.0.3")

    // Argon2 加密库（用于密码哈希）
    implementation("de.mkammerer:argon2-jvm:2.7")

    // TOTP 支持库
    implementation("com.eatthepath:otp-java:1.0.0")

    // 日志
    implementation("ch.qos.logback:logback-classic:1.2.3")

    // 测试相关依赖
    testImplementation("io.ktor:ktor-server-tests:1.6.7")
    testImplementation("org.jetbrains.kotlin:kotlin-test:1.6.21")
}

tasks.withType<Jar> {
    manifest {
        attributes["Main-Class"] = "com.zzszyct.ApplicationKt"
    }
    from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
}