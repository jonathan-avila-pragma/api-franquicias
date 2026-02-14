# 🏢 API de Franquicias

> API REST reactiva desarrollada con **Spring WebFlux** y **Clean Architecture** siguiendo el scaffold de Bancolombia. Sistema de gestión para redes de franquicias con control de sucursales y productos, implementando programación funcional y reactiva con MongoDB como persistencia.

[![Java](https://img.shields.io/badge/Java-17+-orange.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.0.2-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Spring WebFlux](https://img.shields.io/badge/Spring%20WebFlux-Reactive-blue.svg)](https://docs.spring.io/spring-framework/reference/web/webflux.html)
[![MongoDB](https://img.shields.io/badge/MongoDB-Database-green.svg)](https://www.mongodb.com/)
[![Clean Architecture](https://img.shields.io/badge/Architecture-Clean%20Architecture-purple.svg)](https://bancolombia.github.io/scaffold-clean-architecture/)

## 📋 Descripción

API de gestión de franquicias desarrollada siguiendo los principios de **Clean Architecture** y **Arquitectura Hexagonal**. Permite gestionar una red de franquicias donde cada franquicia contiene múltiples sucursales, y cada sucursal maneja un inventario de productos con control de stock.

### ✨ Características Principales

- 🚀 **Programación Reactiva**: Implementada con Spring WebFlux usando `Mono` y `Flux`
- 🏗️ **Clean Architecture**: Separación de responsabilidades siguiendo el scaffold de Bancolombia
- 🔌 **RouterFunctions**: Endpoints implementados con funciones reactivas (sin `@RestController`)
- 💾 **MongoDB**: Persistencia NoSQL con MongoDB (local o MongoDB Atlas gratuito)
- 📚 **OpenAPI/Swagger**: Documentación automática de la API
- 🎯 **Programación Funcional**: Sin programación imperativa, código declarativo
- 🔒 **Manejo de Errores**: Validaciones y mensajes claros desde el dominio
- 📦 **Constantes Centralizadas**: Todas las constantes en una clase separada

## Requisitos

- Java 17 o superior
- Gradle 9.2.1 o superior
- MongoDB (local o MongoDB Atlas - cuenta gratuita disponible)

## Estructura del Proyecto

El proyecto sigue la arquitectura limpia (Clean Architecture) utilizando el scaffold de Bancolombia:

```
api-franquicias/
├── applications/
│   └── app-service/          # Módulo de aplicación principal
├── domain/
│   ├── model/                 # Modelos de dominio y gateways
│   └── usecase/               # Casos de uso del negocio
├── infrastructure/
│   ├── driven-adapters/
│   │   └── mongo-db/          # Adaptador de MongoDB
│   └── entry-points/
│       └── reactive-web/      # Endpoints REST con RouterFunctions
└── deployment/                 # Configuración de despliegue
```

## Modelos de Dominio

- **Franchise**: Representa una franquicia con nombre y lista de sucursales
- **Branch**: Representa una sucursal con nombre y lista de productos
- **Product**: Representa un producto con nombre y stock

## Endpoints Disponibles

### Franquicias

- `GET /api/franchises` - Obtener todas las franquicias
- `POST /api/franchises` - Crear una nueva franquicia
- `PUT /api/franchises/{id}` - Actualizar el nombre de una franquicia

### Sucursales

- `POST /api/franchises/{franchiseId}/branches` - Agregar una sucursal a una franquicia
- `PUT /api/franchises/{franchiseId}/branches/{branchId}` - Actualizar el nombre de una sucursal

### Productos

- `POST /api/franchises/{franchiseId}/branches/{branchId}/products` - Agregar un producto a una sucursal
- `DELETE /api/franchises/{franchiseId}/branches/{branchId}/products/{productId}` - Eliminar un producto
- `PUT /api/franchises/{franchiseId}/branches/{branchId}/products/{productId}` - Actualizar el nombre de un producto
- `PUT /api/franchises/{franchiseId}/branches/{branchId}/products/{productId}/stock` - Modificar el stock de un producto
- `GET /api/franchises/{franchiseId}/max-stock-products` - Obtener el producto con mayor stock por cada sucursal

## Configuración

### MongoDB Local

Para desarrollo local, puedes usar MongoDB local:

1. Instala MongoDB localmente desde [MongoDB](https://www.mongodb.com/try/download/community)
2. Ejecuta MongoDB:
   ```bash
   mongod
   ```

### MongoDB Atlas (Gratis)

Para usar MongoDB en la nube de forma gratuita:

1. Crea una cuenta gratuita en [MongoDB Atlas](https://www.mongodb.com/cloud/atlas/register)
2. Crea un cluster gratuito (M0)
3. Obtén la cadena de conexión desde el dashboard

### Variables de Entorno

El archivo `application.yaml` contiene la configuración. Para desarrollo local:

```yaml
spring:
  data:
    mongodb:
      uri: "mongodb://localhost:27017/api-franquicias"
```

Para MongoDB Atlas, usa la cadena de conexión proporcionada:

```yaml
spring:
  data:
    mongodb:
      uri: "mongodb+srv://usuario:password@cluster.mongodb.net/api-franquicias?retryWrites=true&w=majority"
```

## Ejecución Local

1. Clona el repositorio:
   ```bash
   git clone <repository-url>
   cd api-franquicias
   ```

2. Asegúrate de tener MongoDB ejecutándose localmente (o configura la URI de MongoDB Atlas)

3. Ejecuta la aplicación:
   ```bash
   ./gradlew bootRun
   ```

   O en Windows:
   ```bash
   gradlew.bat bootRun
   ```

4. La aplicación estará disponible en `http://localhost:8080`

## Documentación API

Una vez que la aplicación esté ejecutándose, puedes acceder a:

- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **API Docs**: http://localhost:8080/api-docs

## Estructura de Respuestas

La API utiliza una estructura de respuesta estandarizada para mantener idempotencia y consistencia en todas las respuestas.

### Respuesta Exitosa (ResponseDto<T>)

Todas las respuestas exitosas siguen la siguiente estructura:

```json
{
  "code": "S200000",
  "title": "Successfully",
  "message": "Operación exitosa",
  "data": { ... },
  "errors": null
}
```

**Campos:**
- `code`: Código de negocio que identifica el tipo de operación
- `title`: Título de la respuesta ("Successfully" para operaciones exitosas)
- `message`: Mensaje descriptivo de la operación
- `data`: Datos de respuesta (puede ser un objeto o una lista)
- `errors`: Lista de errores (null en respuestas exitosas)

### Respuesta de Error (ResponseErrorDto)

Todas las respuestas de error siguen la siguiente estructura:

```json
{
  "code": "B400000",
  "title": "Error",
  "message": "Solicitud inválida",
  "errors": ["Mensaje de error específico"]
}
```

**Campos:**
- `code`: Código de error de negocio
- `title`: Título de la respuesta ("Error" para errores)
- `message`: Mensaje descriptivo del error
- `errors`: Lista de mensajes de error específicos

### Códigos de Negocio (BusinessCode)

La API utiliza códigos de negocio estandarizados:

**Códigos de Éxito:**
- `S200000`: Operación exitosa
- `S201000`: Recurso creado exitosamente

**Códigos de Error:**
- `B400000`: Solicitud inválida
- `B400001`: Parámetros requeridos faltantes
- `B400002`: Validación de datos fallida
- `B404000`: Recurso no encontrado
- `B409000`: Conflicto de negocio
- `E500000`: Error interno del servidor

### Códigos HTTP

La API utiliza los siguientes códigos HTTP:

- `200 OK`: Operación exitosa (GET, PUT, DELETE)
- `201 Created`: Recurso creado exitosamente (POST)
- `400 Bad Request`: Solicitud inválida o errores de validación
- `404 Not Found`: Recurso no encontrado
- `409 Conflict`: Conflicto de negocio
- `500 Internal Server Error`: Error interno del servidor

## Estructura de Datos en MongoDB

### Colección: franchises
- **Campo**: `id` (String) - ID único de la franquicia
- **Campo**: `name` (String) - Nombre de la franquicia

### Colección: branches
- **Campo**: `id` (String) - ID único de la sucursal
- **Campo**: `franchiseId` (String) - ID de la franquicia a la que pertenece
- **Campo**: `name` (String) - Nombre de la sucursal

### Colección: products
- **Campo**: `id` (String) - ID único del producto
- **Campo**: `franchiseId` (String) - ID de la franquicia
- **Campo**: `branchId` (String) - ID de la sucursal
- **Campo**: `name` (String) - Nombre del producto
- **Campo**: `stock` (Integer) - Cantidad en stock

## Ejemplos de Uso

### Obtener todas las Franquicias

```bash
curl -X GET http://localhost:8080/api/franchises
```

**Respuesta exitosa (200 OK):**
```json
{
  "code": "S200000",
  "title": "Successfully",
  "message": "Operación exitosa",
  "data": [
    {
      "id": "franchise-1",
      "name": "Mi Franquicia"
    },
    {
      "id": "franchise-2",
      "name": "Otra Franquicia"
    }
  ],
  "errors": null
}
```

### Crear una Franquicia

```bash
curl -X POST http://localhost:8080/api/franchises \
  -H "Content-Type: application/json" \
  -d '{"name": "Mi Franquicia"}'
```

**Respuesta exitosa (201 Created):**
```json
{
  "code": "S201000",
  "title": "Successfully",
  "message": "Recurso creado exitosamente",
  "data": {
    "id": "franchise-1",
    "name": "Mi Franquicia"
  },
  "errors": null
}
```

**Respuesta de error (400 Bad Request):**
```json
{
  "code": "B400000",
  "title": "Error",
  "message": "Solicitud inválida",
  "errors": ["Name cannot be empty"]
}
```

### Agregar una Sucursal

```bash
curl -X POST http://localhost:8080/api/franchises/{franchiseId}/branches \
  -H "Content-Type: application/json" \
  -d '{"name": "Sucursal Centro"}'
```

**Respuesta exitosa (201 Created):**
```json
{
  "code": "S201000",
  "title": "Successfully",
  "message": "Recurso creado exitosamente",
  "data": {
    "id": "branch-1",
    "name": "Sucursal Centro"
  },
  "errors": null
}
```

### Agregar un Producto

```bash
curl -X POST http://localhost:8080/api/franchises/{franchiseId}/branches/{branchId}/products \
  -H "Content-Type: application/json" \
  -d '{"name": "Producto 1", "stock": 100}'
```

**Respuesta exitosa (201 Created):**
```json
{
  "code": "S201000",
  "title": "Successfully",
  "message": "Recurso creado exitosamente",
  "data": {
    "id": "product-1",
    "name": "Producto 1",
    "stock": 100
  },
  "errors": null
}
```

### Obtener Productos con Mayor Stock por Sucursal

```bash
curl -X GET http://localhost:8080/api/franchises/{franchiseId}/max-stock-products
```

**Respuesta exitosa (200 OK):**
```json
{
  "code": "S200000",
  "title": "Successfully",
  "message": "Operación exitosa",
  "data": [
    {
      "product": {
        "id": "product-1",
        "name": "Producto 1",
        "stock": 150
      },
      "branch": {
        "id": "branch-1",
        "name": "Sucursal Centro"
      }
    }
  ],
  "errors": null
}
```

## Características Técnicas

- **Spring WebFlux**: Programación reactiva con RouterFunctions y Handlers
- **Clean Architecture**: Separación de responsabilidades siguiendo el scaffold de Bancolombia
- **MongoDB**: Persistencia NoSQL con MongoDB (Spring Data MongoDB Reactive)
- **OpenAPI/Swagger**: Documentación automática de la API
- **Programación Funcional**: Uso de Mono y Flux para operaciones reactivas
- **Manejo de Errores**: Validaciones y mensajes de error claros
- **Respuestas Estandarizadas**: Estructura de respuesta idempotente con `ResponseDto<T>` y `ResponseErrorDto`
- **Códigos de Negocio**: Sistema de códigos de negocio estandarizados para identificar tipos de operaciones y errores

## Constantes

Todas las constantes del proyecto están centralizadas en la clase `Constants` ubicada en `domain/model/src/main/java/co/com/bancolombia/model/Constants.java`.

## Testing

Para ejecutar los tests:

```bash
./gradlew test
```

## Construcción

Para construir el proyecto:

```bash
./gradlew build
```

El JAR se generará en `applications/app-service/build/libs/`

## Despliegue

El proyecto incluye un `Dockerfile` en la carpeta `deployment/` para facilitar el despliegue en contenedores.

## Notas Importantes

- El proyecto utiliza programación reactiva, evitando programación imperativa
- Las constantes están centralizadas en una clase separada
- Todos los nombres de clases, funciones y variables están en inglés
- Los endpoints utilizan RouterFunctions en lugar de @RestController
- El manejo de errores se realiza desde los handlers y el dominio

## Licencia

Este proyecto es una implementación de ejemplo siguiendo las mejores prácticas de Clean Architecture.
