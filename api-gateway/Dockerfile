# Build stage
FROM maven:3.9.6-eclipse-temurin-21 AS builder

WORKDIR /app

# Copy parent pom
COPY pom.xml .

# Copy common module
COPY common ./common

# Copy api-gateway module
COPY api-gateway ./api-gateway

# Build common first, then api-gateway without invoking the root reactor
RUN mvn -f common/pom.xml clean install -DskipTests \
    && mvn -f api-gateway/pom.xml clean package -DskipTests

# Runtime stage
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Copy JAR from builder
COPY --from=builder /app/api-gateway/target/api-gateway*.jar app.jar

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=40s --retries=3 \
  CMD wget -q -O- http://localhost:8080/eureka || exit 1

ENTRYPOINT ["java", "-jar", "app.jar"]

