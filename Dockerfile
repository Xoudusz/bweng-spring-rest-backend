# Build stage
FROM gradle:8.10.2-jdk21 AS build
WORKDIR /app

COPY build.gradle.kts settings.gradle.kts ./
COPY src ./src

RUN gradle build --no-daemon

# Run stage
FROM openjdk:21-jdk-slim
COPY --from=build /app/build/libs/*.jar /usr/local/lib/app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/usr/local/lib/app.jar"]