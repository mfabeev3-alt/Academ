FROM maven:3.9.6-eclipse-temurin-21-alpine AS build
WORKDIR /app

COPY pom.xml .

# Копируем ВСЕ pom.xml (Maven капризный, ему нужны все упоминания из parent pom)
COPY common-rabbitmq/pom.xml common-rabbitmq/
COPY common-jpa/pom.xml common-jpa/
COPY node/pom.xml node/
COPY dispatcher/pom.xml dispatcher/
COPY rest-service/pom.xml rest-service/

RUN mvn dependency:go-offline -pl dispatcher -am

COPY common-rabbitmq/src common-rabbitmq/src
COPY dispatcher/src dispatcher/src

RUN mvn clean package -DskipTests -pl dispatcher -am

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/dispatcher/target/*.jar app.jar
ENV SPRING_RABBITMQ_HOST=rabbitmq
EXPOSE 8084
ENTRYPOINT ["java", "--enable-preview", "-jar", "app.jar"]