FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app

# Copia TUDO que está na raiz (incluindo src e pom.xml)
COPY . .

# Roda o Maven apontando para o pom.xml que acabou de ser copiado
RUN mvn clean package -DskipTests

FROM eclipse-temurin:17-jre-jammy
WORKDIR /deployments
# O Quarkus gera o resultado em target/quarkus-app/
COPY --from=build /app/target/quarkus-app/ .

EXPOSE 8080
ENTRYPOINT [ "java", "-jar", "quarkus-run.jar" ]