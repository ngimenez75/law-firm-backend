# ==========================================
# Etapa 1: Build (Compilación con JDK 25)
# ==========================================
FROM eclipse-temurin:25-jdk AS builder
WORKDIR /app

# Copiar el wrapper y descriptor de dependencias
COPY .mvn/ .mvn
COPY mvnw pom.xml ./
RUN chmod +x mvnw && ./mvnw dependency:go-offline -B || true

# Copiar el código fuente y empaquetar el JAR
COPY src ./src
RUN ./mvnw clean package -DskipTests

# ==========================================
# Etapa 2: Runtime (Imagen ligera con JRE 25)
# ==========================================
FROM eclipse-temurin:25-jre
WORKDIR /app

# Copiar el JAR generado en la etapa anterior
COPY --from=builder /app/target/*.jar app.jar

# Railway asigna el puerto dinámicamente mediante la variable de entorno $PORT
EXPOSE 8080

# Iniciar la aplicación Spring Boot
ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "app.jar"]
