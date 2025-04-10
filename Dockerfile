# 1단계: 빌드
FROM gradle:7.6.0-jdk17 AS builder
WORKDIR /app

# Gradle 캐시 최적화를 위해 먼저 복사
COPY build.gradle settings.gradle ./
COPY src ./src

# bootJar 빌드
RUN gradle bootJar --no-daemon

# 2단계: 실행
FROM openjdk:17-slim
EXPOSE 8080

# 빌드된 jar 파일을 복사 (jar 파일명 자동 대응)
COPY --from=builder /app/build/libs/*.jar /app/app.jar

# 실행
CMD ["java", "-jar", "/app/app.jar"]