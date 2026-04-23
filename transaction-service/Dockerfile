# Atualizado para Java 21 para compatibilidade com o projeto (pom.xml usa <java.version>21)
FROM eclipse-temurin:21-jdk-alpine

WORKDIR /app

COPY target/*.jar app.jar

EXPOSE 8082

ENTRYPOINT ["java", "-jar", "app.jar"]