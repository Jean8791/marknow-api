# Imagem runtime
FROM eclipse-temurin:17-jre

WORKDIR /app

# Copia o jar gerado pelo build (ajuste o nome)
COPY target/*.jar app.jar

# Render define a porta em $PORT. Seu app TEM que escutar nela.
ENV PORT=8080
EXPOSE 8080

# JVM flags básicas (ajuste se quiser)
ENV JAVA_OPTS="-XX:MaxRAMPercentage=75.0 -Djava.security.egd=file:/dev/./urandom"

CMD ["sh", "-c", "java $JAVA_OPTS -Dserver.port=${PORT} -jar app.jar"]