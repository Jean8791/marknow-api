# -------- build stage --------
FROM maven:3.9.8-eclipse-temurin-17 AS build
WORKDIR /src

COPY pom.xml .
COPY .mvn .mvn
COPY mvnw .
RUN mvn -q -DskipTests dependency:go-offline

COPY src src
RUN mvn -q -DskipTests package

# -------- runtime stage --------
FROM eclipse-temurin:17-jre
WORKDIR /app

COPY --from=build /src/target/quarkus-app/ /app/quarkus-app/

# Render injeta $PORT. Quarkus precisa ouvir nela.
ENV PORT=8080
EXPOSE 8080

# Flags básicas de memória
ENV JAVA_OPTS="-XX:MaxRAMPercentage=75.0"

CMD ["sh","-c","java $JAVA_OPTS -Dquarkus.http.host=0.0.0.0 -Dquarkus.http.port=${PORT} -jar /app/quarkus-app/quarkus-run.jar"]