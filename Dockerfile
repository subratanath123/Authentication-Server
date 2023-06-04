FROM openjdk:17-jdk-slim
CMD ["gradle", "build"]
COPY /build/libs/AuthorizationServer-0.0.1-SNAPSHOT.jar /app/AuthorizationServer.jar
EXPOSE 8000
ENTRYPOINT ["java", "-jar", "/app/AuthorizationServer.jar"]
