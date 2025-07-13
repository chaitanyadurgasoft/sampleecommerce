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

# Create a new stage for running the application (multi-stage build)
FROM openjdk:17-jdk-slim

# Set working directory
WORKDIR /app

# Copy the built JAR file from the previous stage
COPY --from=build /app/target/*.jar app.jar

# Expose port 8082
EXPOSE 8082

# Set JVM options for better container performance
ENV JAVA_OPTS="-Xmx512m -Xms256m"

# Set notification service URL environment variable
ENV NOTIFICATION_SERVICE_URL=http://notification-service:8083

# Run the application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -Dnotification.service.url=$NOTIFICATION_SERVICE_URL -jar app.jar"]
