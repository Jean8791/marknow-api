# Estágio 1: Build (Usando Maven com Java 17)
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /home/app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Estágio 2: Runtime (Usando JRE 17 para rodar)
FROM eclipse-temurin:17-jre-jammy
WORKDIR /deployments
ENV JAVA_OPTIONS="-Dquarkus.http.host=0.0.0.0 -Djava.util.logging.manager=org.jboss.logmanager.LogManager"
COPY --from=build /home/app/target/quarkus-app/ .
EXPOSE 8080
ENTRYPOINT [ "java", "-jar", "quarkus-run.jar" ]