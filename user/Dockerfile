FROM maven:3.9.6-eclipse-temurin-21 AS build

WORKDIR /app

COPY . .

RUN mvn clean package -pl user -am -DskipTests

FROM eclipse-temurin:21-jdk

WORKDIR /app

COPY --from=build /app/user/target/*.jar app.jar

EXPOSE 8001

ENTRYPOINT ["java","-jar","app.jar"]

#FROM eclipse-temurin:21-jdk-alpine
#WORKDIR /app
#COPY target/*.jar app.jar
#EXPOSE 8001
#ENTRYPOINT ["java","-jar","/app/app.jar"]
