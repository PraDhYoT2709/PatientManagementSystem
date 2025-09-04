FROM openjdk:21-jdk-slim

WORKDIR /app

COPY target/discovery-service-1.0.0.jar app.jar

EXPOSE 8761

ENTRYPOINT ["java", "-jar", "app.jar"]