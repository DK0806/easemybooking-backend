# booking-service/Dockerfile
FROM eclipse-temurin:21-jre
WORKDIR /app

# copy the fat jar produced by spring-boot-maven-plugin:repackage
COPY target/*.jar app.jar

EXPOSE 8084
ENTRYPOINT ["java","-jar","/app/app.jar"]
