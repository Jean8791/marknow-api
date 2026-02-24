# Estágio 1: Build
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app

# Copia tudo da raiz do seu git para dentro de /app no container
COPY . .

# LISTA OS ARQUIVOS (Isso vai aparecer no seu log do Render para debugarmos)
RUN ls -la

# Roda o Maven garantindo que ele procure o pom.xml na pasta atual
RUN mvn clean package -DskipTests

# Estágio 2: Runtime
FROM eclipse-temurin:17-jre-jammy
WORKDIR /deployments

# Copia o resultado do build do estágio anterior
COPY --from=build /app/target/quarkus-app/ .

EXPOSE 8080
ENTRYPOINT [ "java", "-jar", "quarkus-run.jar" ]