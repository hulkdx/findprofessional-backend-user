FROM eclipse-temurin:19.0.1_10-jre-alpine

COPY user-service/build/libs/*.jar app.jar

ENTRYPOINT [ "java", "-jar", "app.jar" ]
