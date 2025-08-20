# auth-service/Dockerfile
FROM eclipse-temurin:21-jre
WORKDIR /app
# copy whatever jar was built
COPY target/*.jar app.jar
# your app listens on 8081 (from application.yml)
EXPOSE 8081
ENTRYPOINT ["java","-jar","/app/app.jar"]
