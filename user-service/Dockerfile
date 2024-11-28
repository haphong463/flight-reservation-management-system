FROM openjdk:17
EXPOSE 8080
ADD target/user-service.jar user-service.jar
ENTRYPOINT ["java", "-jar", "/user-service.jar"]