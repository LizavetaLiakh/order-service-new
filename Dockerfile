FROM maven:3.9.3-eclipse-temurin-20 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -U -DskipTests

FROM eclipse-temurin:20-jre-alpine
WORKDIR /app
COPY --from=build /app/target/ord2-1.0-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]