# Dockerfile
FROM maven:3.9.6-eclipse-temurin-17 AS build

# Set working directory
WORKDIR /app

# Copy Maven project files
COPY pom.xml .

# Download dependencies (cached if no change in pom.xml)
RUN mvn dependency:go-offline

# Copy the entire project
COPY . .

# Build the Spring Boot application (skip tests for faster build)
RUN mvn clean package -DskipTests

# Create runtime stage
FROM openjdk:17-jdk-slim

# Set working directory
WORKDIR /app

# Copy the built JAR file from build stage
COPY --from=build /app/target/*.jar app.jar

# Expose port 8083
EXPOSE 8083

# Set JVM options for container performance
ENV JAVA_OPTS="-Xmx512m -Xms256m"

# Run the application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
