FROM eclipse-temurin:17-jre-jammy

WORKDIR /app

COPY target/celestix-1.0.0-SNAPSHOT.jar app.jar

EXPOSE 8087

ENTRYPOINT ["java", "-jar", "app.jar"]
