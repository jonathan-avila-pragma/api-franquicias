# Scripts de Prueba de Carga

Scripts para probar la generación de IDs incrementales con concurrencia.

## Opción 1: Postman (Pre-request Script)

### Configuración en Postman:

1. Crea un nuevo request en Postman:
   - **Method**: POST
   - **URL**: `http://localhost:8080/api/franchises`
   - **Headers**: 
     - `Content-Type: application/json`
     - `Accept: application/json`

2. **Body** (raw JSON):
```json
{
  "name": "{{franchise_name}}"
}
```

3. **Pre-request Script** (pestaña "Pre-request Script"):
   - Copia el contenido de `postman-pre-request.js`
   - Este script generará automáticamente un nombre aleatorio `fra1` a `fra1000`

4. **Collection Runner**:
   - Ve a la colección → "Run"
   - Configura el número de iteraciones (ej: 100)
   - Ejecuta para hacer múltiples requests

## Opción 2: PowerShell - Prueba Simple

Script secuencial para pruebas rápidas:

```powershell
.\scripts\load-test-simple.ps1 -Count 50
```

**Parámetros:**
- `-Count`: Número de franquicias a crear (default: 50)
- `-BaseUrl`: URL base de la API (default: http://localhost:8080)

## Opción 3: PowerShell - Prueba de Carga Completa

Script con concurrencia para pruebas de carga intensivas:

```powershell
.\scripts\load-test.ps1 -TotalRequests 100 -ConcurrentRequests 10
```

**Parámetros:**
- `-TotalRequests`: Total de requests a enviar (default: 100)
- `-ConcurrentRequests`: Número de requests concurrentes (default: 10)
- `-BaseUrl`: URL base de la API (default: http://localhost:8080)

**Ejemplo de prueba intensiva:**
```powershell
.\scripts\load-test.ps1 -TotalRequests 500 -ConcurrentRequests 50
```

## Verificación

Los scripts verifican automáticamente:
- ✅ IDs únicos (sin duplicados)
- ✅ Tasa de éxito/fallo
- ✅ Performance (requests por segundo)
- ✅ Generación de reporte CSV

## Notas

- Asegúrate de que la aplicación esté corriendo en `http://localhost:8080`
- Los scripts generan nombres aleatorios entre `fra1` y `fra1000`
- Los IDs deberían ser incrementales: 1, 2, 3, 4, ...
- Con concurrencia, los IDs pueden no estar en orden, pero deben ser únicos
