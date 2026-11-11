# ---------- Etapa 1: compila o projeto com Maven ----------
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

# Copia primeiro o pom.xml para aproveitar o cache das dependencias
COPY pom.xml .
RUN mvn -B -q dependency:go-offline

COPY src ./src
RUN mvn -B -q -DskipTests package

# ---------- Etapa 2: imagem final, so com o JRE e o .jar ----------
FROM eclipse-temurin:21-jre
# Fuso de Sao Paulo: regras de horario (ex.: penalidade noturna depois das 18h) usam a hora local
ENV TZ=America/Sao_Paulo
WORKDIR /app
COPY --from=build /app/target/rota-para-todos-api.jar app.jar

# O Render informa a porta pela variavel PORT (application.properties ja le essa variavel)
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
