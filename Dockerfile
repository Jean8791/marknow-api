# Estágio 1: Build
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /home/app

# Copia o arquivo de configuração primeiro
COPY pom.xml /home/app/

# Copia a pasta de código (garanta que o nome é 'src' minúsculo)
COPY src /home/app/src

# Roda o Maven
RUN mvn -f /home/app/pom.xml clean package -DskipTests

# Estágio 2: Runtime
FROM eclipse-temurin:17-jre-jammy
WORKDIR /deployments
ENV JAVA_OPTIONS="-Dquarkus.http.host=0.0.0.0 -Djava.util.logging.manager=org.jboss.logmanager.LogManager"

# Copia o resultado do build
COPY --from=build /home/app/target/quarkus-app/ /deployments/

EXPOSE 8080
ENTRYPOINT [ "java", "-jar", "/deployments/quarkus-run.jar" ]