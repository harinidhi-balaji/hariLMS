# Use OpenJDK 17 as base image
FROM openjdk:17-jdk-slim

# Set working directory
WORKDIR /app

# Install curl for health checks
RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*

# Copy maven executable to the image
COPY mvnw .
COPY .mvn .mvn

# Copy the pom.xml file
COPY pom.xml .

# Copy source code
COPY src src

# Make the maven wrapper executable
RUN chmod +x ./mvnw

# Build the application (this will also download dependencies)
RUN ./mvnw clean package -DskipTests

# Expose port 8080
EXPOSE 8080

# Set environment variables
ENV SPRING_PROFILES_ACTIVE=docker
ENV JAVA_OPTS="-Xmx512m -Xms256m"

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
    CMD curl -f http://localhost:8080/actuator/health || exit 1

# Run the jar file
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar target/lms-1.0.0.jar"]