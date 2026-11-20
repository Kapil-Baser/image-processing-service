FROM eclipse-temurin:25-jdk-alpine AS build-stage
WORKDIR /build

COPY .mvn .mvn/
COPY mvnw pom.xml ./

RUN ./mvnw dependency:go-offline

COPY src ./src/
RUN ./mvnw package -DskipTests

FROM eclipse-temurin:25-jre-alpine
WORKDIR /app

COPY --from=build-stage /build/target/*.jar image-processing-service.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "image-processing-service.jar"]