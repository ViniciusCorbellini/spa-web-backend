# Etapa 1: Build da aplicação
FROM maven:3.9.6-eclipse-temurin-21 AS build

WORKDIR /app

COPY pom.xml .
RUN mvn -q dependency:go-offline

COPY src ./src

RUN mvn -q package -DskipTests

# Etapa 2: Imagem final (super leve)
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Caminho onde o jar será copiado
COPY --from=build /app/target/*.jar app.jar

# Diretório de uploads (Render monta /data,
# mas localmente ainda funciona)
RUN mkdir -p /data/uploads && chmod 777 /data/uploads

ENV JAVA_OPTS="-Xmx512m"

EXPOSE 8080

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
