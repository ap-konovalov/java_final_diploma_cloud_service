FROM openjdk:17.0.1-jdk-slim
EXPOSE 5500
ADD build/libs/cloud-service-0.0.1-SNAPSHOT.jar cloud-service.jar
CMD ["java" , "-jar", "cloud-service.jar"]