# Atualizado para Java 21 para compatibilidade com o projeto (pom.xml usa <java.version>21)
FROM maven:3.9.6-eclipse-temurin-21 AS build

WORKDIR /app

COPY . .

RUN echo "=== ROOT ==="
RUN ls -la

RUN echo "=== APP ==="
RUN ls -la /app

RUN echo "=== TX ==="
RUN ls -la /app/transaction-service

RUN cat /app/pom.xml

RUN mvn clean package -pl transaction-service -am -DskipTests

FROM eclipse-temurin:21-jdk

WORKDIR /app

COPY --from=build /app/transaction-service/target/*.jar app.jar

EXPOSE 8002

ENTRYPOINT ["java","-jar","app.jar"]
#FROM maven:3.9.6-eclipse-temurin-21 AS build
#
#WORKDIR /app
#COPY . .
#RUN mvn clean package -DskipTests
#
#FROM eclipse-temurin:21-jdk
#
#WORKDIR /app
#COPY --from=build /app/target/*.jar app.jar
#
#EXPOSE 8002
#ENTRYPOINT ["java","-jar","app.jar"]