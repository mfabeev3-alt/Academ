FROM maven:3.9.6-eclipse-temurin-21-alpine AS build
WORKDIR /app

# Шаг 1: Копируем РОДИТЕЛЬСКИЙ pom.xml
COPY pom.xml .

# Шаг 2: Копируем ВСЕ pom.xml модулей (обязательно для всех сервисов)
COPY common-rabbitmq/pom.xml common-rabbitmq/
COPY common-jpa/pom.xml common-jpa/
COPY node/pom.xml node/
COPY dispatcher/pom.xml dispatcher/
COPY rest-service/pom.xml rest-service/

# Шаг 3: Теперь Maven увидит структуру и скачает зависимости
RUN mvn dependency:go-offline -pl node -am

# Шаг 4: Копируем только те исходники, которые нужны для сборки node
COPY common-rabbitmq/src common-rabbitmq/src
COPY common-jpa/src common-jpa/src
COPY node/src node/src

RUN mvn clean package -DskipTests -pl node -am

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/node/target/*.jar app.jar
EXPOSE 8085
ENTRYPOINT ["java", "--enable-preview", "-jar", "app.jar"]