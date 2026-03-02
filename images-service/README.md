# Images Service

Microservicio para gestión de imágenes con Spring Boot, MongoDB y Redis.

## Descripción

API REST para subir, listar, obtener, actualizar y descargar imágenes. Utiliza MongoDB para persistencia de metadatos y Redis como caché.

## Requisitos

- Java 17
- Maven 3.9+
- MongoDB (puerto 27017)
- Redis (puerto 6379)

## Endpoints

| Método | Ruta | Descripción |
|--------|------|-------------|
| `POST` | `/api/images` | Subir imagen (multipart/form-data) |
| `GET` | `/api/images` | Listar todas las imágenes |
| `GET` | `/api/images/{id}` | Obtener una imagen por ID |
| `PUT` | `/api/images/{id}` | Actualizar imagen |
| `DELETE` | `/api/images/{id}` | Borrar imagen |
| `GET` | `/api/images/{id}/download` | Descargar archivo |

## Configuración

El servidor se inicia en el **puerto 8081**.

### application.properties

```properties
server.port=8081
spring.data.mongodb.host=localhost
spring.data.mongodb.port=27017
spring.data.mongodb.database=images_db
spring.data.redis.host=localhost
spring.data.redis.port=6379
images.upload.dir=./uploads
```

## Ejecución

```bash
mvn spring-boot:run
```

## Tests

```bash
mvn test
```

Cobertura mínima requerida: **80%**

## Estructura

```
src/
├── main/java/com/images/
│   ├── ImagesServiceApplication.java
│   ├── controller/ImageController.java
│   ├── model/Image.java
│   ├── repository/ImageRepository.java
│   ├── service/
│   │   ├── ImageService.java
│   │   └── ImageServiceImpl.java
│   ├── config/
│   │   ├── RedisConfig.java
│   │   ├── SecurityConfig.java
│   │   └── CorsConfig.java
│   ├── exception/ImageException.java
│   └── dto/
│       ├── ImageRequest.java
│       └── ImageResponse.java
└── test/java/com/images/
    ├── controller/ImageControllerTest.java
    ├── service/ImageServiceTest.java
    └── repository/ImageRepositoryTest.java
```
