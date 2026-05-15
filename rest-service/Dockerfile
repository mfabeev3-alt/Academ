# ===== Stage 1: сборка =====
FROM maven:3.9.6-eclipse-temurin-21 AS builder

WORKDIR /app

# копируем весь Maven multi-module проект
COPY . .

# собираем только нужный модуль
RUN mvn clean package -pl rest-service -am -DskipTests


# ===== Stage 2: запуск =====
FROM eclipse-temurin:21-jdk

WORKDIR /app

# забираем готовый jar из сборки
COPY --from=build /app/rest-service/target/*.jar app.jar

EXPOSE 8086

ENTRYPOINT ["java", "--enable-preview", "-jar", "app.jar"]