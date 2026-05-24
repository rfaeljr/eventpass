FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN ./mvnw -q package -DskipTests 2>/dev/null || \
    (apk add --no-cache maven && mvn -q package -DskipTests)

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
RUN addgroup -S eventpass && adduser -S eventpass -G eventpass
COPY --from=builder /app/target/*.jar app.jar
USER eventpass
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=${SPRING_PROFILES_ACTIVE:dev}", "app.jar"]
