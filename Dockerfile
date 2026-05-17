# ─────────────────────────────────────────────────────────────
# Stage 1: BUILD — uses Maven + Java 21 to compile and package
# ─────────────────────────────────────────────────────────────
FROM maven:3.9.6-eclipse-temurin-21 AS build

WORKDIR /app

# Copy Maven config first (better Docker layer caching)
COPY pom.xml .
COPY .mvn .mvn
COPY mvnw .
COPY mvnw.cmd .

# Download dependencies first (cached unless pom.xml changes)
RUN mvn dependency:go-offline -q

# Copy source code
COPY src ./src

# Build the JAR (skip tests for faster build)
RUN mvn clean package -DskipTests -q

# ─────────────────────────────────────────────────────────────
# Stage 2: RUN — lightweight Java 21 image, just runs the JAR
# ─────────────────────────────────────────────────────────────
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Copy only the built JAR from stage 1
COPY --from=build /app/target/library-management-system-1.0.0.jar app.jar

# Expose the application port
EXPOSE 8085

# Start the Spring Boot application
ENTRYPOINT ["java", "-jar", "app.jar"]
