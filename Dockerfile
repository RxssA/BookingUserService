# Use an OpenJDK image as the base
FROM openjdk:17-jdk-slim

# Set the working directory inside the container
WORKDIR /app

# Copy the project JAR file into the container
COPY target/UserService-0.0.1-SNAPSHOT.jar /app/user-service.jar


# Expose the port the service runs on
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "user-service.jar"]
