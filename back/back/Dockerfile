# Étape 1 : Base image
FROM eclipse-temurin:17-jdk

# Étape 2 : Dossier de travail
WORKDIR /app

# Étape 3 : Copier le jar
COPY target/*.jar app.jar

# Étape 4 : Exposer le port de Spring Boot
EXPOSE 8080

# Étape 5 : Lancer l'application
ENTRYPOINT ["java", "-jar", "app.jar"]
