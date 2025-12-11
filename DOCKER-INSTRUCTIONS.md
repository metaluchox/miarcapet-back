# Instrucciones para Docker Desktop

## Requisitos previos
- Tener Docker Desktop instalado y en ejecución
- Tener tu contenedor PostgreSQL (mi-postgres-db) corriendo
- Tener el código fuente de miarcapet en tu máquina

## Opción 1: Usando Docker Compose (RECOMENDADO)

### Iniciar la aplicación
```bash
# Asegúrate de que tu PostgreSQL esté corriendo
docker ps | grep mi-postgres-db

# Navega al directorio del proyecto
cd /home/metaluchox/Documentos/IDE/miarcapet

# Construye y levanta la aplicación (usa tu PostgreSQL existente)
docker-compose up --build
```

### Comandos útiles de Docker Compose

```bash
# Levantar en segundo plano (modo detached)
docker-compose up -d

# Ver los logs en tiempo real
docker-compose logs -f

# Ver logs solo de la aplicación
docker-compose logs -f app

# Ver logs solo de PostgreSQL
docker-compose logs -f postgres

# Detener los servicios
docker-compose stop

# Detener y eliminar los contenedores
docker-compose down

# Detener, eliminar contenedores Y ELIMINAR VOLÚMENES (¡cuidado, borra la BD!)
docker-compose down -v

# Reconstruir la imagen si cambiaste el código
docker-compose up --build

# Ver el estado de los contenedores
docker-compose ps
```

## Opción 2: Usando solo Docker (sin PostgreSQL)

### Construir la imagen
```bash
# Navega al directorio del proyecto
cd /home/metaluchox/Documentos/IDE/miarcapet

# Construye la imagen Docker
docker build -t miarcapet:latest .
```

### Ejecutar el contenedor
```bash
# Ejecuta el contenedor mapeando el puerto 8080
docker run -p 8080:8080 --name miarcapet-app miarcapet:latest
```

### Con variables de entorno personalizadas
```bash
docker run -p 8080:8080 \
  -e SPRING_PROFILES_ACTIVE=dev \
  -e SPRING_DATASOURCE_URL=jdbc:h2:mem:testdb \
  --name miarcapet-app \
  miarcapet:latest
```

## Acceder a la aplicación

Una vez que el contenedor esté corriendo:

- **Aplicación Spring Boot**: http://localhost:8080
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **PostgreSQL** (tu contenedor existente): localhost:5432
  - Database: `miarcapet`
  - Usuario: `postgres`
  - Contraseña: `tito`

## Verificar que todo funciona

```bash
# Verificar que los contenedores están corriendo
docker ps

# Ver los logs de la aplicación
docker logs -f miarcapet-app

# Verificar la salud de PostgreSQL
docker exec miarcapet-postgres pg_isready -U miarcapet_user
```

## Usar Docker Desktop (GUI)

1. Abre Docker Desktop
2. Ve a la pestaña "Containers"
3. Deberías ver:
   - `miarcapet-app` (tu aplicación Spring Boot)
   - `miarcapet-postgres` (base de datos PostgreSQL)
4. Puedes:
   - Ver logs haciendo clic en el contenedor
   - Detener/iniciar contenedores
   - Abrir el navegador directamente desde la interfaz

## Solución de problemas

### La aplicación no se conecta a PostgreSQL
```bash
# Verifica que tu PostgreSQL esté corriendo
docker ps | grep mi-postgres-db

# Revisa los logs de PostgreSQL
docker logs mi-postgres-db

# Verifica que ambos contenedores estén en la misma red
docker inspect mi-postgres-db --format='{{range $k, $v := .NetworkSettings.Networks}}{{$k}}{{end}}'
docker inspect miarcapet-app --format='{{range $k, $v := .NetworkSettings.Networks}}{{$k}}{{end}}'

# Verifica la conexión desde el contenedor de la app
docker exec miarcapet-app ping -c 2 mi-postgres-db
```

### Necesitas reconstruir después de cambios en el código
```bash
# Detén los contenedores
docker-compose down

# Reconstruye la imagen
docker-compose build --no-cache

# Inicia nuevamente
docker-compose up
```

### Limpieza completa
```bash
# Elimina contenedores, redes y volúmenes
docker-compose down -v

# Elimina imágenes sin usar
docker image prune -a

# Limpieza completa de Docker
docker system prune -a --volumes
```

## Configuración de producción

Para producción, recuerda:

1. **NO** incluir credenciales directamente en `docker-compose.yml`
2. Usar un archivo `.env` para las variables sensibles
3. Configurar volúmenes de backup para PostgreSQL
4. Ajustar la memoria y recursos en Docker Desktop (Settings > Resources)
5. Usar perfiles de Spring Boot apropiados (prod, staging, etc.)

## Ejemplo de archivo .env (RECOMENDADO)

Crea un archivo `.env` en el mismo directorio del proyecto:

```env
# Variables de la aplicación
SPRING_PROFILES_ACTIVE=prod

# Credenciales de PostgreSQL (¡NO SUBIR A GIT!)
DB_HOST=mi-postgres-db
DB_PORT=5432
DB_NAME=miarcapet
DB_USER=postgres
DB_PASSWORD=tito

# Configuración de JWT (ejemplo)
JWT_SECRET=tu_secreto_super_seguro_aqui
JWT_EXPIRATION=86400000
```

Y modifica el `docker-compose.yml` para usar estas variables:
```yaml
app:
  environment:
    SPRING_PROFILES_ACTIVE: ${SPRING_PROFILES_ACTIVE}
    SPRING_DATASOURCE_URL: jdbc:postgresql://${DB_HOST}:${DB_PORT}/${DB_NAME}
    SPRING_DATASOURCE_USERNAME: ${DB_USER}
    SPRING_DATASOURCE_PASSWORD: ${DB_PASSWORD}
```

**IMPORTANTE:** Agrega `.env` a tu `.gitignore` para no subir credenciales a Git
