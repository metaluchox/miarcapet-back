# ====================================
# ETAPA 1: Build (Compilación)
# ====================================
# Usa una imagen de Maven con JDK 21 para compilar el proyecto
FROM maven:3.9-eclipse-temurin-21 AS build

# Establece el directorio de trabajo dentro del contenedor
# Todos los comandos siguientes se ejecutarán en este directorio
WORKDIR /app

# Copia el archivo pom.xml primero (optimización de cache de Docker)
# Si solo cambian los archivos de código y no las dependencias,
# Docker reutilizará las capas de dependencias ya descargadas
COPY pom.xml .

# Descarga todas las dependencias del proyecto
# El flag -B ejecuta Maven en modo batch (sin interacción)
# El flag -e muestra errores detallados
RUN mvn dependency:go-offline -B -e

# Copia todo el código fuente del proyecto al contenedor
# El primer punto (.) es la carpeta actual del host
# El segundo punto (.) es el WORKDIR del contenedor (/app)
COPY src ./src

# Compila el proyecto y genera el archivo WAR
# -DskipTests omite la ejecución de tests para acelerar el build
# clean: limpia compilaciones anteriores
# package: compila y empaqueta la aplicación en un WAR
RUN mvn clean package -DskipTests

# ====================================
# ETAPA 2: Runtime (Ejecución)
# ====================================
# Usa una imagen más liviana solo con JRE 21 para ejecutar la aplicación
# Esto reduce significativamente el tamaño final de la imagen
FROM eclipse-temurin:21-jre-jammy

# Establece el directorio de trabajo para la aplicación
WORKDIR /app

# Copia el archivo WAR compilado desde la etapa de build
# --from=build indica que copiamos desde la primera etapa
# El WAR generado por Maven está en target/[nombre-del-archivo].war
COPY --from=build /app/target/miarcapet-0.0.1-SNAPSHOT.war app.war

# Expone el puerto 8080 (puerto por defecto de Spring Boot)
# Esto documenta que la aplicación escucha en este puerto
# Docker Desktop podrá mapear este puerto al host
EXPOSE 8080

# Define variables de entorno para la JVM
# -Djava.security.egd: mejora el rendimiento de generación de números aleatorios
ENV JAVA_OPTS="-Djava.security.egd=file:/dev/./urandom"

# Comando que se ejecuta cuando se inicia el contenedor
# java: ejecuta la JVM
# $JAVA_OPTS: aplica las opciones de la JVM definidas arriba
# -jar app.war: ejecuta el archivo WAR de Spring Boot
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.war"]
