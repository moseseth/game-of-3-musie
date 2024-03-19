FROM maven:3.8.3-openjdk-17-slim AS build
WORKDIR /app
COPY . .
RUN mvn clean package

FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=build /app/target/gameof3-1.0-SNAPSHOT-jar-with-dependencies.jar /app/gameof3.jar
COPY .env /app/.env
ENTRYPOINT ["java", "-jar", "gameof3.jar"]
