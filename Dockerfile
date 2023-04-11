FROM openjdk:17
ADD target/recommendation-service-0.0.1-SNAPSHOT.jar app.jar
COPY src/main/resources/prices/* prices/
EXPOSE 8080
CMD ["java", "-Ddirectory.path=prices/", "-jar", "app.jar"]