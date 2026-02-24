FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /workspace

# Copia apenas o pom primeiro
COPY pom.xml .
# Tenta copiar a pasta src (usando o caminho relativo correto)
COPY src ./src

RUN mvn clean package -DskipTests

FROM eclipse-temurin:17-jre-jammy
WORKDIR /deployments
COPY --from=build /workspace/target/quarkus-app/ .

EXPOSE 8080
ENTRYPOINT [ "java", "-jar", "quarkus-run.jar" ]