FROM maven:3.9.4-eclipse-temurin-17 AS build
WORKDIR /workspace

# Copy pom and sources and build the Spring Boot fat jar
COPY pom.xml ./
COPY src ./src
RUN mvn -B -DskipTests package

FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
# Copy the fat jar produced by the build stage
ARG JAR_FILE=target/backned-0.0.1-SNAPSHOT.jar
COPY --from=build /workspace/${JAR_FILE} /app/app.jar

EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]
