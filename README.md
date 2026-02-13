# 🏢 API de Franquicias

> API REST reactiva desarrollada con **Spring WebFlux** y **Clean Architecture** siguiendo el scaffold de Bancolombia. Sistema de gestión para redes de franquicias con control de sucursales y productos, implementando programación funcional y reactiva con DynamoDB como persistencia.

[![Java](https://img.shields.io/badge/Java-17+-orange.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.0.2-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Spring WebFlux](https://img.shields.io/badge/Spring%20WebFlux-Reactive-blue.svg)](https://docs.spring.io/spring-framework/reference/web/webflux.html)
[![DynamoDB](https://img.shields.io/badge/DynamoDB-AWS-yellow.svg)](https://aws.amazon.com/dynamodb/)
[![Clean Architecture](https://img.shields.io/badge/Architecture-Clean%20Architecture-purple.svg)](https://bancolombia.github.io/scaffold-clean-architecture/)

## 📋 Descripción

API de gestión de franquicias desarrollada siguiendo los principios de **Clean Architecture** y **Arquitectura Hexagonal**. Permite gestionar una red de franquicias donde cada franquicia contiene múltiples sucursales, y cada sucursal maneja un inventario de productos con control de stock.

### ✨ Características Principales

- 🚀 **Programación Reactiva**: Implementada con Spring WebFlux usando `Mono` y `Flux`
- 🏗️ **Clean Architecture**: Separación de responsabilidades siguiendo el scaffold de Bancolombia
- 🔌 **RouterFunctions**: Endpoints implementados con funciones reactivas (sin `@RestController`)
- 💾 **DynamoDB**: Persistencia NoSQL escalable en AWS
- 📚 **OpenAPI/Swagger**: Documentación automática de la API
- 🎯 **Programación Funcional**: Sin programación imperativa, código declarativo
- 🔒 **Manejo de Errores**: Validaciones y mensajes claros desde el dominio
- 📦 **Constantes Centralizadas**: Todas las constantes en una clase separada

## Requisitos

- Java 17 o superior
- Gradle 9.2.1 o superior
- DynamoDB (local o en AWS)

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
│   │   └── dynamo-db/         # Adaptador de DynamoDB
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

### DynamoDB Local

Para desarrollo local, configura DynamoDB local:

1. Descarga DynamoDB Local desde [AWS](https://docs.aws.amazon.com/amazon-dynamodb/latest/developerguide/DynamoDBLocal.html)
2. Ejecuta DynamoDB Local:
   ```bash
   java -Djava.library.path=./DynamoDBLocal_lib -jar DynamoDBLocal.jar -sharedDb
   ```

3. Configura las credenciales de AWS (para local):
   ```bash
   aws configure
   ```

### Variables de Entorno

El archivo `application.yaml` contiene la configuración. Para desarrollo local:

```yaml
aws:
  region: "us-east-1"
  dynamodb:
    endpoint: "http://localhost:8000"
```

Para producción, elimina la configuración de `endpoint` y usa las credenciales de AWS configuradas.

## Ejecución Local

1. Clona el repositorio:
   ```bash
   git clone <repository-url>
   cd api-franquicias
   ```

2. Asegúrate de tener DynamoDB Local ejecutándose (o configura las credenciales de AWS)

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

## Estructura de Datos en DynamoDB

### Tabla: Franchises
- **Partition Key**: `id` (String)

### Tabla: Branches
- **Partition Key**: `franchiseId` (String)
- **Sort Key**: `id` (String)

### Tabla: Products
- **Partition Key**: `compositeKey` (String) - Formato: `{franchiseId}#{branchId}`
- **Sort Key**: `id` (String)

## Ejemplos de Uso

### Crear una Franquicia

```bash
curl -X POST http://localhost:8080/api/franchises \
  -H "Content-Type: application/json" \
  -d '{"name": "Mi Franquicia"}'
```

### Agregar una Sucursal

```bash
curl -X POST http://localhost:8080/api/franchises/{franchiseId}/branches \
  -H "Content-Type: application/json" \
  -d '{"name": "Sucursal Centro"}'
```

### Agregar un Producto

```bash
curl -X POST http://localhost:8080/api/franchises/{franchiseId}/branches/{branchId}/products \
  -H "Content-Type: application/json" \
  -d '{"name": "Producto 1", "stock": 100}'
```

### Obtener Productos con Mayor Stock por Sucursal

```bash
curl -X GET http://localhost:8080/api/franchises/{franchiseId}/max-stock-products
```

## Características Técnicas

- **Spring WebFlux**: Programación reactiva con RouterFunctions y Handlers
- **Clean Architecture**: Separación de responsabilidades siguiendo el scaffold de Bancolombia
- **DynamoDB**: Persistencia NoSQL con AWS DynamoDB
- **OpenAPI/Swagger**: Documentación automática de la API
- **Programación Funcional**: Uso de Mono y Flux para operaciones reactivas
- **Manejo de Errores**: Validaciones y mensajes de error claros

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
