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


FROM openjdk:17-jdk-slim

# Set working directory
WORKDIR /app

# Copy the built JAR file from the previous stage
COPY --from=build /app/target/*.jar app.jar

# Expose port 8081 (not 8080)
EXPOSE 8081

# Set JVM options for better container performance
ENV JAVA_OPTS="-Xmx512m -Xms256m"

# Set payment service URL environment variable
ENV PAYMENT_SERVICE_URL=http://payment-service:8082

# Run the application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -Dpayment.service.url=$PAYMENT_SERVICE_URL -jar app.jar"]
