# Estágio 1: Build
FROM maven:3.9.6-eclipse-temurin-21 AS build
COPY src /home/app/src
COPY pom.xml /home/app
RUN mvn -f /home/app/pom.xml clean package -DskipTests

# Estágio 2: Runtime
FROM eclipse-temurin:21-jre-jammy
ENV JAVA_OPTIONS="-Dquarkus.http.host=0.0.0.0 -Djava.util.logging.manager=org.jboss.logmanager.LogManager"
COPY --from=build /home/app/target/quarkus-app/ /deployments/
EXPOSE 8080
ENTRYPOINT [ "java", "-jar", "/deployments/quarkus-run.jar" ]