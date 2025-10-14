# Use an official OpenJDK runtime as a parent image
FROM openjdk:17-slim

# Set the working directory inside the container
WORKDIR /app

# Copy the Maven wrapper and pom.xml to leverage Docker layer caching
COPY .mvn/ .mvn
COPY mvnw pom.xml ./

# Download project dependencies
RUN ./mvnw dependency:go-offline

# Copy the rest of your application's source code
COPY src ./src

# Package the application into a .jar file
RUN ./mvnw package -DskipTests

# Expose the port your Spring Boot app runs on
EXPOSE 8080

# The command to run your application
ENTRYPOINT ["java", "-jar", "target/your-project-name-0.0.1-SNAPSHOT.jar"]