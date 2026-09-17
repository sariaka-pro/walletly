# ── BUILD STAGE ──
FROM maven:3.9-eclipse-temurin-17 AS builder

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
# La variante Jammy est publiée pour AMD64 et ARM64 (Mac Apple Silicon).
FROM eclipse-temurin:17-jre-jammy

WORKDIR /app

# Copier le JAR buildé
COPY --from=builder /app/target/*.jar app.jar

# Exposer le port
EXPOSE 8081

# Commande de démarrage
ENTRYPOINT ["java", "-jar", "app.jar"]
