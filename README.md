# API de Generación de Códigos QR

Esta es una API RESTful construida con Spring Boot que permite a los usuarios registrarse, autenticarse y generar códigos QR a partir de un texto proporcionado. La aplicación está securizada mediante JSON Web Tokens (JWT) y está completamente containerizada con Docker para un despliegue y desarrollo sencillos.

## ✨ Características

-   **Autenticación de Usuarios**: Endpoints para registro (`/register`) y login (`/login`).
-   **Seguridad con JWT**: Los endpoints de la API están protegidos. El acceso se concede a través de un Bearer Token JWT que se obtiene al hacer login.
-   **Generación de Códigos QR**: Endpoint principal para generar una imagen de código QR en formato PNG a partir de un texto.
-   **Caché con Redis**: Utiliza Redis para cachear las respuestas y mejorar el rendimiento.
-   **Persistencia con PostgreSQL**: Almacena la información de los usuarios en una base de datos PostgreSQL.
-   **Containerización**: Configuración completa con `docker-compose` para levantar la aplicación, la base de datos y el servidor de Redis con un solo comando.

## ⚡ Versión Lightweight

Si buscas una versión más simple y ligera de esta API sin base de datos, seguridad ni caché, [puedes probar la versión lightweight aquí](https://github.com/youlserf/qr-service-lightweight). Esta variante está enfocada únicamente en la generación de códigos QR con una configuración mínima para facilitar el despliegue rápido y pruebas.

## 🛠️ Tecnologías Utilizadas

-   **Backend**: Java 21, Spring Boot 3
-   **Seguridad**: Spring Security, JWT
-   **Base de Datos**: Spring Data JPA, PostgreSQL
-   **Caché**: Spring Cache, Redis
-   **Generación de QR**: ZXing (o una librería similar)
-   **Build Tool**: Maven / Gradle
-   **Containerización**: Docker, Docker Compose

## 🚀 Puesta en Marcha (Getting Started)

### Prerrequisitos

-   JDK 17 o superior
-   Docker y Docker Compose
-   Maven o Gradle

### Opción 1: Ejecutar con Docker (Recomendado)

Esta es la forma más sencilla de levantar todo el entorno (aplicación, base de datos y Redis) sin necesidad de instalar nada más localmente.

1.  **Clona el repositorio:**
    ```bash
    git clone <URL_DEL_REPOSITORIO>
    cd <NOMBRE_DEL_DIRECTORIO>
    ```

2.  **Levanta los servicios con Docker Compose:**
    Este comando construirá la imagen de la aplicación y levantará los tres contenedores (`app`, `db`, `redis`) en segundo plano.
    ```bash
    docker-compose up --build -d
    ```

3.  **¡Listo!** La aplicación estará disponible en `http://localhost:8080`.

Para detener todos los servicios, ejecuta:
```bash
docker-compose down
```

### Opción 2: Ejecutar Localmente (Modo Desarrollo)

Si prefieres no usar Docker, puedes ejecutar la aplicación localmente. Deberás tener PostgreSQL y Redis instalados y corriendo en sus puertos por defecto.

1.  **Asegúrate de tener PostgreSQL y Redis ejecutándose localmente:**
    -   PostgreSQL en `localhost:5432`
    -   Redis en `localhost:6379`

2.  **Configura la aplicación para el perfil `dev`:**
    El archivo `src/main/resources/application-dev.properties` (o `.yml`) debe contener la configuración para conectarse a tus servicios locales:

    ```properties
    # src/main/resources/application-dev.properties
    spring.datasource.url=jdbc:postgresql://localhost:5432/qrdb
    spring.datasource.username=postgres
    spring.datasource.password=postgres
    spring.jpa.hibernate.ddl-auto=update

    spring.redis.host=localhost
    spring.redis.port=6379
    spring.cache.type=redis

    jwt.secret=5a71a1ec2c314f79f732a1d9bd51fb32
    jwt.expiration=3600000

    server.port=8080
    ```

3.  **Compila y ejecuta la aplicación:**
    Puedes ejecutar la aplicación desde tu IDE o usando Maven, asegurándote de activar el perfil `dev`.
    ```bash
    mvn spring-boot:run -Dspring-boot.run.profiles=dev
    ```

## ⚙️ Uso de la API (Endpoints)

### Autenticación

#### 1. Registrar un nuevo usuario

-   **Endpoint**: `POST /api/auth/register`
-   **Descripción**: Crea un nuevo usuario en el sistema.
-   **Body**:
    ```json
    {
      "username": "tu_usuario",
      "password": "tu_password"
    }
    ```
-   **Respuesta**: `201 Created` si el registro es exitoso.

#### 2. Iniciar sesión

-   **Endpoint**: `POST /api/auth/login`
-   **Descripción**: Autentica a un usuario y devuelve un token JWT.
-   **Body**:
    ```json
    {
      "username": "tu_usuario",
      "password": "tu_password"
    }
    ```
-   **Respuesta Exitosa (`200 OK`)**:
    ```json
    {
      "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ5b3UiLCJpYXQiOjE2..."
    }
    ```
    **Guarda este token.** Lo necesitarás para acceder a las rutas protegidas.

### Generación de QR

Estos endpoints requieren autenticación. Debes incluir el token JWT en la cabecera `Authorization`.

-   **Cabecera de Autorización**: `Authorization: Bearer <TU_TOKEN_JWT>`

#### 1. Generar un código QR

-   **Endpoint**: `GET /api/qr`
-   **Descripción**: Genera una imagen PNG de un código QR con el texto proporcionado.
-   **Parámetros (Query Param)**:
    -   `text` (string, requerido): El texto que se codificará en el QR.
-   **Ejemplo de Petición**:
    ```http
    GET http://localhost:8080/api/qr?text=HolaMundoConSpring
    Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ5b3UiLCJpYXQiOjE2...
    ```
-   **Respuesta**: Una imagen PNG (`image/png`) del código QR.

#### 2. Generar un código QR (con medición)

-   **Endpoint**: `GET /api/qr/measure`
-   **Descripción**: Genera una imagen PNG de un código QR y realiza una medición interna (ej. tiempo de generación).
-   **Parámetros (Query Param)**:
    -   `text` (string, requerido): El texto que se codificará en el QR.
-   **Ejemplo de Petición**:
    ```http
    GET http://localhost:8080/api/qr/measure?text=OtroTextoParaMedir
    Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ5b3UiLCJpYXQiOjE2...
    ```
-   **Respuesta**: Una imagen PNG (`image/png`) del código QR.

## 📝 Configuración

La configuración de la aplicación se gestiona a través de variables de entorno cuando se utiliza Docker, lo que sigue las mejores prácticas de 12-Factor App.

Las principales variables de entorno se definen en el archivo `docker-compose.yml`:

-   `DB_HOST`: Host de la base de datos (ej. `db`).
-   `DB_PORT`: Puerto de la base de datos (ej. `5432`).
-   `DB_NAME`: Nombre de la base de datos (ej. `qrdb`).
-   `DB_USER`: Usuario de la base de datos (ej. `postgres`).
-   `DB_PASS`: Contraseña de la base de datos.
-   `REDIS_HOST`: Host de Redis (ej. `redis`).
-   `REDIS_PORT`: Puerto de Redis (ej. `6379`).
-   `JWT_SECRET`: Clave secreta para firmar los tokens JWT.
-   `JWT_EXPIRATION`: Tiempo de expiración del token en milisegundos.