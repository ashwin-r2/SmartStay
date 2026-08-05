# --- Build stage -----------------------------------------------------------
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

# Cache dependencies separately from source for faster rebuilds
COPY pom.xml .
RUN mvn -q -B dependency:go-offline

COPY src ./src
RUN mvn -q -B -DskipTests package

# --- Runtime stage -----------------------------------------------------------
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

RUN addgroup -S staysmart && adduser -S staysmart -G staysmart
COPY --from=build /app/target/staysmart-ai-backend.jar app.jar
RUN mkdir -p /app/uploads && chown -R staysmart:staysmart /app
USER staysmart

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
