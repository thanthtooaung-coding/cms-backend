FROM eclipse-temurin:17-jre-jammy

WORKDIR /app

COPY target/email-service-1.0.0-SNAPSHOT.jar app.jar

EXPOSE 8085

ENTRYPOINT ["java", "-jar", "app.jar"]
