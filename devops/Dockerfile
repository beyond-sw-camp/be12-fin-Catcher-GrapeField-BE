# 1단계: 빌드
FROM openjdk:17-slim AS builder
WORKDIR /app

# Gradle Wrapper 관련 파일 복사
COPY gradlew gradlew.bat ./
COPY gradle/wrapper ./gradle/wrapper

# 프로젝트 설정 파일 복사
COPY build.gradle settings.gradle ./

# 소스 복사
COPY src ./src

# 실행 권한 부여 및 빌드
RUN chmod +x gradlew
RUN ./gradlew bootJar --no-daemon

# 2단계: 실행
FROM openjdk:17-slim
WORKDIR /app
EXPOSE 8080

COPY --from=builder /app/build/libs/*.jar /app/app.jar
CMD ["java", "-jar", "/app/app.jar"]
