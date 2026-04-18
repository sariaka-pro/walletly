# ── BUILD STAGE ──
FROM maven:3.9-eclipse-temurin-21 AS builder

WORKDIR /app

# Copier les fichiers Maven
COPY backend/pom.xml .
COPY backend/mvnw .
COPY backend/mvnw.cmd .
COPY backend/.mvn ./.mvn

# Copier le code source
COPY backend/src ./src

# Builder l'application
RUN mvn clean package -DskipTests

# ── PRODUCTION STAGE ──
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Copier le JAR buildé
COPY --from=builder /app/target/*.jar app.jar

# Exposer le port
EXPOSE 8080

# Commande de démarrage
ENTRYPOINT ["java", "-jar", "app.jar"]
