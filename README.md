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

- **Franchise**: Representa una franquicia con nombre, descripción y lista de sucursales
- **Branch**: Representa una sucursal con nombre, dirección, ciudad y lista de productos
- **Product**: Representa un producto con nombre y stock

## Endpoints Disponibles

### Franquicias

- `GET /api/franchises` - Obtener todas las franquicias
- `GET /api/franchises/{franchiseId}` - Obtener una franquicia por ID
- `POST /api/franchises` - Crear una nueva franquicia (requiere: name, description opcional)
- `PUT /api/franchises/{franchiseId}` - Actualizar el nombre de una franquicia
- `GET /api/franchises/{franchiseId}/max-stock-products` - Obtener el producto con mayor stock por cada sucursal

### Sucursales

- `GET /api/franchises/{franchiseId}/branches/{branchId}` - Obtener una sucursal por ID
- `POST /api/franchises/{franchiseId}/branches` - Agregar una sucursal a una franquicia (requiere: name, address y city opcionales)
- `PUT /api/franchises/{franchiseId}/branches/{branchId}` - Actualizar sucursal (campos opcionales: name, address, city - al menos uno requerido)

### Productos

- `GET /api/franchises/{franchiseId}/branches/{branchId}/products/name/{productName}` - Obtener un producto por nombre
- `POST /api/franchises/{franchiseId}/branches/{branchId}/products` - Agregar un producto a una sucursal
- `DELETE /api/franchises/{franchiseId}/branches/{branchId}/products/{productId}` - Eliminar un producto
- `PUT /api/franchises/{franchiseId}/branches/{branchId}/products/{productId}` - Actualizar el nombre de un producto
- `PUT /api/franchises/{franchiseId}/branches/{branchId}/products/{productId}/stock` - Modificar el stock de un producto

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

El proyecto utiliza variables de entorno para configurar la conexión a MongoDB. Puedes configurarlas de las siguientes formas:

#### Opción 1: Variables de Entorno del Sistema

```bash
export MONGODB_HOST=pruebas.kuhx1fs.mongodb.net
export MONGODB_USERNAME=tu_usuario
export MONGODB_PASSWORD=tu_contraseña
export MONGODB_DATABASE=api-franquicias
```

#### Opción 2: Archivo .env (Recomendado para desarrollo local)

Crea un archivo `.env` en la raíz del proyecto con:

```env
MONGODB_HOST=pruebas.kuhx1fs.mongodb.net
MONGODB_USERNAME=tu_usuario
MONGODB_PASSWORD=tu_contraseña
MONGODB_DATABASE=api-franquicias
```

#### Opción 3: Valores por Defecto en application.yaml

El archivo `application.yaml` contiene valores por defecto que se pueden sobrescribir con variables de entorno:

```yaml
spring:
  data:
    mongodb:
      host: ${MONGODB_HOST:pruebas.kuhx1fs.mongodb.net}
      username: ${MONGODB_USERNAME:jonathanavila83_db_user}
      password: ${MONGODB_PASSWORD:oKHayRCVJG2YiFAh}
      database: ${MONGODB_DATABASE:api-franquicias}
      uri: mongodb+srv://${spring.data.mongodb.username}:${spring.data.mongodb.password}@${spring.data.mongodb.host}/${spring.data.mongodb.database}?retryWrites=true&w=majority
```

#### Para MongoDB Local

Si usas MongoDB local, configura:

```bash
export MONGODB_HOST=localhost:27017
export MONGODB_USERNAME=
export MONGODB_PASSWORD=
export MONGODB_DATABASE=api-franquicias
```

Y actualiza el `application.yaml` para usar `mongodb://` en lugar de `mongodb+srv://`:

```yaml
spring:
  data:
    mongodb:
      uri: mongodb://${spring.data.mongodb.host}/${spring.data.mongodb.database}
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
- `B503000`: Servicio temporalmente no disponible (Circuit Breaker abierto)
- `E500000`: Error interno del servidor

### Códigos HTTP

La API utiliza los siguientes códigos HTTP:

- `200 OK`: Operación exitosa (GET, PUT, DELETE)
- `201 Created`: Recurso creado exitosamente (POST)
- `400 Bad Request`: Solicitud inválida o errores de validación
- `404 Not Found`: Recurso no encontrado
- `409 Conflict`: Conflicto de negocio
- `503 Service Unavailable`: Servicio temporalmente no disponible (Circuit Breaker abierto)
- `500 Internal Server Error`: Error interno del servidor

## Estructura de Datos en MongoDB

### Colección: franchises
- **Campo**: `id` (String) - ID único de la franquicia (generado incrementalmente)
- **Campo**: `name` (String) - Nombre de la franquicia (requerido)
- **Campo**: `description` (String) - Descripción de la franquicia (opcional, máximo 500 caracteres)

### Colección: branches
- **Campo**: `id` (String) - ID único de la sucursal (generado incrementalmente)
- **Campo**: `franchiseId` (String) - ID de la franquicia a la que pertenece
- **Campo**: `name` (String) - Nombre de la sucursal (requerido)
- **Campo**: `address` (String) - Dirección de la sucursal (opcional, máximo 200 caracteres)
- **Campo**: `city` (String) - Ciudad de la sucursal (opcional, máximo 100 caracteres)

### Colección: products
- **Campo**: `id` (String) - ID único del producto (generado incrementalmente)
- **Campo**: `franchiseId` (String) - ID de la franquicia
- **Campo**: `branchId` (String) - ID de la sucursal
- **Campo**: `name` (String) - Nombre del producto (requerido)
- **Campo**: `stock` (Integer) - Cantidad en stock (requerido, rango: 0-999999)

### Colección: sequences
- **Campo**: `id` (String) - Nombre de la secuencia (franchise_sequence, branch_sequence, product_sequence)
- **Campo**: `sequence` (Long) - Valor actual de la secuencia (usado para generar IDs incrementales)

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
      "id": "1",
      "name": "Mi Franquicia",
      "description": "Descripción de la franquicia",
      "branches": []
    },
    {
      "id": "2",
      "name": "Otra Franquicia",
      "description": null,
      "branches": []
    }
  ],
  "errors": null
}
```

### Crear una Franquicia

```bash
curl -X POST http://localhost:8080/api/franchises \
  -H "Content-Type: application/json" \
  -d '{"name": "Mi Franquicia", "description": "Descripción de la franquicia"}'
```

**Respuesta exitosa (201 Created):**
```json
{
  "code": "S201000",
  "title": "Successfully",
  "message": "Recurso creado exitosamente",
  "data": {
    "id": "1",
    "name": "Mi Franquicia",
    "description": "Descripción de la franquicia",
    "branches": []
  },
  "errors": null
}
```

### Obtener una Franquicia por ID

```bash
curl -X GET http://localhost:8080/api/franchises/1
```

**Respuesta exitosa (200 OK):**
```json
{
  "code": "S200000",
  "title": "Successfully",
  "message": "Operación exitosa",
  "data": {
    "id": "1",
    "name": "Mi Franquicia",
    "description": "Descripción de la franquicia",
    "branches": []
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
curl -X POST http://localhost:8080/api/franchises/1/branches \
  -H "Content-Type: application/json" \
  -d '{"name": "Sucursal Centro", "address": "Calle 123 #45-67", "city": "Bogotá"}'
```

**Respuesta exitosa (201 Created):**
```json
{
  "code": "S201000",
  "title": "Successfully",
  "message": "Recurso creado exitosamente",
  "data": {
    "id": "1",
    "name": "Sucursal Centro",
    "address": "Calle 123 #45-67",
    "city": "Bogotá",
    "products": []
  },
  "errors": null
}
```

### Obtener una Sucursal por ID

```bash
curl -X GET http://localhost:8080/api/franchises/1/branches/1
```

**Respuesta exitosa (200 OK):**
```json
{
  "code": "S200000",
  "title": "Successfully",
  "message": "Operación exitosa",
  "data": {
    "id": "1",
    "name": "Sucursal Centro",
    "address": "Calle 123 #45-67",
    "city": "Bogotá",
    "products": []
  },
  "errors": null
}
```

### Actualizar una Sucursal (Campos Opcionales)

```bash
# Actualizar solo el nombre
curl -X PUT http://localhost:8080/api/franchises/1/branches/1 \
  -H "Content-Type: application/json" \
  -d '{"name": "Sucursal Norte"}'

# Actualizar solo la dirección
curl -X PUT http://localhost:8080/api/franchises/1/branches/1 \
  -H "Content-Type: application/json" \
  -d '{"address": "Av. 100 #50-30"}'

# Actualizar nombre y ciudad
curl -X PUT http://localhost:8080/api/franchises/1/branches/1 \
  -H "Content-Type: application/json" \
  -d '{"name": "Sucursal Sur", "city": "Medellín"}'

# Actualizar todos los campos
curl -X PUT http://localhost:8080/api/franchises/1/branches/1 \
  -H "Content-Type: application/json" \
  -d '{"name": "Sucursal Principal", "address": "Carrera 15 #30-20", "city": "Cali"}'
```

**Respuesta exitosa (200 OK):**
```json
{
  "code": "S200000",
  "title": "Successfully",
  "message": "Operación exitosa",
  "data": {
    "id": "1",
    "name": "Sucursal Principal",
    "address": "Carrera 15 #30-20",
    "city": "Cali",
    "products": []
  },
  "errors": null
}
```

**Nota**: Los campos no enviados en el request se conservan con sus valores actuales. Al menos uno de los campos (name, address, o city) debe ser proporcionado.

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
    "id": "1",
    "name": "Producto 1",
    "stock": 100
  },
  "errors": null
}
```

### Obtener un Producto por Nombre

```bash
curl -X GET http://localhost:8080/api/franchises/1/branches/1/products/name/Producto%201
```

**Respuesta exitosa (200 OK):**
```json
{
  "code": "S200000",
  "title": "Successfully",
  "message": "Operación exitosa",
  "data": {
    "id": "1",
    "name": "Producto 1",
    "stock": 150
  },
  "errors": null
}
```

### Obtener Productos con Mayor Stock por Sucursal

```bash
curl -X GET http://localhost:8080/api/franchises/1/max-stock-products
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
        "id": "1",
        "name": "Producto 1",
        "stock": 150
      },
      "branch": {
        "id": "1",
        "name": "Sucursal Centro",
        "address": "Calle 123 #45-67",
        "city": "Bogotá"
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
- **Circuit Breaker**: Implementado con Resilience4j para proteger contra fallos en cascada
- **Seguridad**: Validación y sanitización de inputs para prevenir NoSQL Injection
- **OpenAPI/Swagger**: Documentación automática de la API
- **Programación Funcional**: Uso de Mono y Flux para operaciones reactivas, evitando programación imperativa
- **Manejo de Errores**: Validaciones y mensajes de error claros
- **Respuestas Estandarizadas**: Estructura de respuesta idempotente con `ResponseDto<T>` y `ResponseErrorDto`
- **Códigos de Negocio**: Sistema de códigos de negocio estandarizados para identificar tipos de operaciones y errores
- **IDs Incrementales**: Generación atómica de IDs incrementales usando MongoDB `findAndModify` para evitar problemas de concurrencia
- **Actualizaciones Parciales**: Soporte para actualizar solo campos específicos (ej: actualizar solo nombre, dirección o ciudad de una sucursal)
- **Validación de Entrada**: Jakarta Validation con anotaciones para validar DTOs de request

## Constantes

Todas las constantes del proyecto están centralizadas en la clase `Constants` ubicada en `domain/model/src/main/java/co/com/pragma/model/Constants.java`.

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

## Circuit Breaker

El proyecto implementa un **Circuit Breaker** usando Resilience4j para proteger contra fallos en cascada cuando MongoDB no está disponible o presenta problemas.

### Configuración

El circuit breaker está configurado con los siguientes parámetros:

- **Failure Rate Threshold**: 50% - Se abre cuando el 50% de las llamadas fallan
- **Sliding Window Size**: 10 llamadas - Ventana deslizante para evaluar fallos
- **Minimum Number of Calls**: 5 - Mínimo de llamadas antes de evaluar
- **Wait Duration in Open State**: 30 segundos - Tiempo de espera antes de intentar cerrar
- **Permitted Calls in Half-Open**: 3 - Llamadas permitidas en estado half-open

### Comportamiento

Cuando el circuit breaker está **abierto** (OPEN):
- Las llamadas a MongoDB son rechazadas inmediatamente
- Se retorna un error `503 Service Unavailable` con código de negocio `B503000`
- El mensaje indica que el servicio está temporalmente no disponible

Cuando el circuit breaker está **cerrado** (CLOSED):
- Las operaciones funcionan normalmente
- Se monitorean los fallos para detectar problemas

Cuando el circuit breaker está en **half-open**:
- Se permiten algunas llamadas de prueba
- Si son exitosas, el circuit breaker se cierra
- Si fallan, vuelve a abrirse

### Monitoreo

El estado del circuit breaker puede ser monitoreado a través de:

- **Actuator Endpoint**: `http://localhost:8080/actuator/health` - Incluye el estado del circuit breaker
- **Circuit Breakers Endpoint**: `http://localhost:8080/actuator/circuitbreakers` - Detalles del circuit breaker
- **Logs**: Los cambios de estado se registran en los logs de la aplicación

## Seguridad y Prevención de Inyección

El proyecto implementa múltiples capas de seguridad para prevenir **NoSQL Injection** y otros ataques de inyección:

### Validación de Entrada

- **Jakarta Validation**: Todos los DTOs de request tienen validaciones con anotaciones (`@NotBlank`, `@Size`, `@Pattern`, `@Min`, `@Max`)
- **Sanitización de Inputs**: La clase `InputSanitizer` valida y sanitiza todos los inputs del usuario
- **Validación de Path Variables**: Todos los IDs en las URLs son validados antes de ser usados en consultas

### Protecciones Implementadas

1. **Validación de Patrones Peligrosos**:
   - Detecta y rechaza operadores MongoDB (`$gt`, `$ne`, `$regex`, etc.)
   - Previene inyección de objetos y arrays maliciosos
   - Bloquea caracteres especiales peligrosos

2. **Validación de IDs**:
   - Solo permite caracteres alfanuméricos, guiones y guiones bajos
   - Longitud máxima de 50 caracteres
   - Formato estricto para prevenir manipulación

3. **Validación de Nombres**:
   - Solo permite caracteres alfanuméricos, espacios, puntos, guiones y guiones bajos
   - Longitud máxima de 100 caracteres
   - Sanitización automática de espacios

4. **Validación de Números**:
   - Rango válido para stock: 0 a 999999
   - Validación de tipos antes de procesar

5. **Queries Parametrizadas**:
   - Todas las consultas a MongoDB usan `ReactiveMongoTemplate` con queries parametrizadas
   - No se construyen queries dinámicamente con concatenación de strings

### Ejemplos de Ataques Prevenidos

```bash
# Intento de NoSQL Injection - RECHAZADO
POST /api/franchises
{"name": "test' || '1'=='1"}

# Intento con operadores MongoDB - RECHAZADO  
GET /api/franchises/{$ne: null}

# Intento con caracteres peligrosos - RECHAZADO
POST /api/franchises
{"name": "<script>alert('xss')</script>"}
```

Todos estos intentos son detectados y rechazados con un error `400 Bad Request` antes de llegar a la base de datos.

## Generación de IDs Incrementales

El proyecto utiliza generación atómica de IDs incrementales para evitar problemas de concurrencia:

- **Franquicias**: Secuencia independiente `franchise_sequence`
- **Sucursales**: Secuencia independiente `branch_sequence`
- **Productos**: Secuencia independiente `product_sequence`

Cada secuencia se almacena en la colección `sequences` de MongoDB y se incrementa de forma atómica usando `findAndModify`, garantizando que no habrá duplicados incluso bajo alta concurrencia.

## Notas Importantes

- El proyecto utiliza programación reactiva y funcional, evitando programación imperativa
- Las constantes están centralizadas en una clase separada
- Todos los nombres de clases, funciones y variables están en inglés
- Los endpoints utilizan RouterFunctions en lugar de @RestController
- El manejo de errores se realiza desde los handlers y el dominio
- El circuit breaker protege contra fallos en cascada cuando MongoDB no está disponible
- Todas las entradas del usuario son validadas y sanitizadas para prevenir NoSQL injection
- Los IDs se generan de forma incremental y atómica para evitar problemas de concurrencia
- Las actualizaciones parciales permiten modificar solo los campos necesarios sin afectar los demás
- Los campos opcionales (description en Franchise, address y city en Branch) permiten enriquecer la información sin ser obligatorios

## Licencia

Este proyecto es una implementación de ejemplo siguiendo las mejores prácticas de Clean Architecture.
