FROM eclipse-temurin:25-jdk-alpine

WORKDIR /app

COPY pom.xml .
COPY src ./src

ARG MAVEN_ARGS="-DskipTests clean package"
RUN apk add --no-cache maven && \
    mvn ${MAVEN_ARGS} && \
    cp target/*.jar app.jar && \
    rm -rf src target && \
    apk del maven

ENTRYPOINT ["java", "-jar", "app.jar"]

EXPOSE 8080