# Build stage
FROM maven:3.9.6-eclipse-temurin-21 AS builder

WORKDIR /app

# Copy parent pom
COPY pom.xml .

# Copy common module
COPY common ./common

# Copy service-discovery module
COPY service-discovery ./service-discovery

# Build common and service-discovery
RUN mvn clean install -DskipTests -pl common,service-discovery -am

# Copy the rest
COPY . .

# Build service-discovery JAR
RUN mvn clean package -DskipTests -pl service-discovery -am

# Runtime stage
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Copy JAR from builder
COPY --from=builder /app/service-discovery/target/service-discovery*.jar app.jar

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=40s --retries=3 \
  CMD wget -q -O- http://localhost:8761/eureka || exit 1

ENTRYPOINT ["java", "-jar", "app.jar"]

