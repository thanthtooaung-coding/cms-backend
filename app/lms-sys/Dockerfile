FROM eclipse-temurin:17-jre-jammy

WORKDIR /app

COPY target/lms-1.0.0-SNAPSHOT.jar app.jar

EXPOSE 8082

ENTRYPOINT ["java", "-jar", "app.jar"]
