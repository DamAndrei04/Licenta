# ── Stage 1: Build backend and publish to Maven local ─────────────────────────
# MAS depends on com.example:demo:0.0.1-SNAPSHOT, so we must publish the
# backend artifact before the MAS build can resolve its dependencies.
FROM eclipse-temurin:17-jdk-alpine AS backend-build
WORKDIR /backend

COPY backend/gradlew .
COPY backend/gradle gradle/
COPY backend/build.gradle .
COPY backend/settings.gradle .
RUN chmod +x gradlew && ./gradlew dependencies --no-daemon

COPY backend/src src/
RUN ./gradlew publishToMavenLocal -x test --no-daemon

# ── Stage 2: Build MAS ─────────────────────────────────────────────────────────
FROM eclipse-temurin:17-jdk-alpine AS build
WORKDIR /app

# Bring the backend artifact into this stage's Maven local cache
COPY --from=backend-build /root/.m2 /root/.m2

COPY mas/gradlew .
COPY mas/gradle gradle/
COPY mas/build.gradle .
COPY mas/settings.gradle .
RUN chmod +x gradlew && ./gradlew dependencies --no-daemon

COPY mas/src src/
RUN ./gradlew bootJar -x test --no-daemon

# ── Stage 3: Runtime ───────────────────────────────────────────────────────────
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/build/libs/mas-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]
