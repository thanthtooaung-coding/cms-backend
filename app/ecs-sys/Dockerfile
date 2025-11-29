FROM eclipse-temurin:17-jre-jammy

WORKDIR /app

COPY target/ecs-1.0.0-SNAPSHOT.jar app.jar

EXPOSE 8088

ENTRYPOINT ["java", "-jar", "app.jar"]

