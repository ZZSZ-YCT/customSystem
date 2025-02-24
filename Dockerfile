# 第一阶段：使用 Gradle 镜像构建项目
FROM gradle:7.4.2-jdk11 AS builder
WORKDIR /app
COPY . .
RUN gradle build --no-daemon

# 第二阶段：使用 OpenJDK 镜像运行应用
FROM openjdk:11-jre-slim
WORKDIR /app
# 将构建产物复制到运行镜像中
COPY --from=builder /app/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
