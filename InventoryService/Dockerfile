FROM eclipse-temurin:21-jdk

WORKDIR /app

COPY target/*.jar app.jar

EXPOSE 8083

EXPOSE 9091

ENTRYPOINT ["java","-jar","app.jar"]