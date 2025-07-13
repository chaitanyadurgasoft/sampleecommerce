# ---------------------------------------
# Base image with OpenJDK 17 and Maven
# ---------------------------------------
FROM maven:3.9.6-eclipse-temurin-17 AS build

# Set working directory inside the container
WORKDIR /app

# Copy Maven project files
COPY pom.xml .

# Download dependencies (cached if no change in pom.xml)
RUN mvn dependency:go-offline

# Copy the entire project
COPY . .

# Build the Spring Boot application (skip tests for faster build)
RUN mvn clean package -DskipTests


# ---------------------------------------
# Final stage: runtime image
# ---------------------------------------
FROM openjdk:17-jdk-slim

# Set working directory for runtime
WORKDIR /app

# Copy only the built JAR from the build stage
COPY --from=build /app/target/*.jar app.jar

# Expose the Spring Boot default port
EXPOSE 8080

# Optional: set JVM options
ENV JAVA_OPTS="-Xmx512m -Xms256m"

# Start the Spring Boot application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
