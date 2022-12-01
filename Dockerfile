FROM amazoncorretto:19.0.1-alpine

COPY user-service/build/libs/*.jar app.jar

ENTRYPOINT [ "java", "-jar", "app.jar" ]
