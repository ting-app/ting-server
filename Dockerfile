FROM maven:3.8.6-eclipse-temurin-17

WORKDIR /app
ADD . /app

RUN mvn clean install -DskipTests=true spring-boot:repackage

EXPOSE 8080

CMD ["java", "-jar", "target/ting-0.0.1-SNAPSHOT.jar"]