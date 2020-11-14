FROM openjdk:8-jdk-alpine
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar
ENV LISTEN_PORT=8080
ENTRYPOINT ["java","-jar", "-Dserver.port=${LISTEN_PORT}", "/app.jar"]
