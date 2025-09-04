FROM openjdk:21-jdk-slim

WORKDIR /app

COPY target/auth-service-1.0.0.jar app.jar

EXPOSE 8084

ENTRYPOINT ["java", "-jar", "app.jar"]