# Use a imagem base do OpenJDK 17
FROM openjdk:17

# Define o diretório de trabalho dentro do contêiner
WORKDIR /app

# Copia o arquivo JAR gerado pelo Maven para o contêiner
COPY target/moneyTracker-0.0.1-SNAPSHOT.jar app.jar
COPY src/main/resources/db/migration /app/resources/db/migration

# Expõe a porta 8081 para acesso externo
EXPOSE 8080

# Comando para executar a aplicação
CMD ["java", "-jar", "app.jar"]