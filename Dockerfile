FROM maven:3.9.6-eclipse-temurin-21 AS build

# Crear directorio de trabajo
WORKDIR /app

# Copiar pom.xml y descargar dependencias (cachear en capas)
COPY pom.xml .
RUN mvn dependency:go-offline

# Copiar código fuente
COPY src ./src

# Comando por defecto: correr la app
CMD ["mvn", "spring-boot:run"]
