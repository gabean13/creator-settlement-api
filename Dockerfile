# syntax=docker/dockerfile:1.7

# ===== Build stage =====
FROM eclipse-temurin:21-jdk AS builder
WORKDIR /app

# Gradle wrapper 및 빌드 설정 우선 복사 → 의존성 레이어 캐시 활용
COPY gradlew gradlew
COPY gradle gradle
COPY build.gradle settings.gradle ./
RUN chmod +x ./gradlew \
    && ./gradlew --no-daemon dependencies > /dev/null 2>&1 || true

# 소스 복사 후 bootJar 빌드 (build 태스크의 installGitHooks 의존성 우회)
COPY src src
RUN ./gradlew --no-daemon clean bootJar -x test

# ===== Runtime stage =====
FROM eclipse-temurin:21-jre
WORKDIR /app

RUN groupadd --system spring \
    && useradd --system --gid spring --home-dir /app --shell /usr/sbin/nologin spring

COPY --from=builder /app/build/libs/*.jar /app/app.jar
RUN chown spring:spring /app/app.jar

USER spring:spring

ENV TZ=Asia/Seoul \
    JAVA_OPTS=""

EXPOSE 8080

ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar /app/app.jar"]
