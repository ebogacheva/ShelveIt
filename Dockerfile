# Build stage
FROM maven:3.9.6-eclipse-temurin-21 AS build

WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline -B
COPY src ./src
RUN mvn clean package -DskipTests

# Runtime stage
FROM eclipse-temurin:21-jre
WORKDIR /app

COPY --from=build /app/target/ShelveIt.jar ShelveIt.jar

EXPOSE 8080

# Default entrypoint for web mode
ENTRYPOINT ["java", "-jar", "ShelveIt.jar"]
CMD ["--spring.profiles.active=dev"]