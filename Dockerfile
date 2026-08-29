FROM maven:3.9.9-eclipse-temurin-17 AS build
WORKDIR /workspace

COPY pom.xml .
RUN mvn -B -q -DskipTests dependency:go-offline

COPY src ./src
RUN mvn -B -q -DskipTests package

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

RUN addgroup -S expense && adduser -S expense -G expense
COPY --from=build /workspace/target/expense-tracker-0.0.1-SNAPSHOT.jar app.jar

USER expense
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
