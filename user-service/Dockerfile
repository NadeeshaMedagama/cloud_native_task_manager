# Build stage
FROM maven:3.9.6-eclipse-temurin-21 AS builder

WORKDIR /app

# Copy parent pom
COPY pom.xml .

# Copy common module
COPY common ./common

# Copy user-service module
COPY user-service ./user-service

# Build common first, then user-service without invoking the root reactor
RUN mvn -f common/pom.xml clean install -DskipTests \
    && mvn -f user-service/pom.xml clean package -DskipTests

# Runtime stage
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Copy JAR from builder
COPY --from=builder /app/user-service/target/user-service*.jar app.jar

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=40s --retries=3 \
  CMD wget -q -O- http://localhost:8083/api/v3/api-docs || exit 1

ENTRYPOINT ["java", "-jar", "app.jar"]

