# Instrucciones para Ejecutar la Prueba de Carga

Esta colección de Postman crea automáticamente:
- **100 franquicias**
- **10-20 sucursales** por cada franquicia (aleatorio)
- **10+ productos** por cada sucursal (aleatorio entre 10-20)
- **Stock aleatorio** entre 10-500 unidades por producto

Los productos pueden repetirse entre diferentes sucursales para mayor realismo.

## Requisitos Previos

1. **Postman** instalado (versión 8.0 o superior)
2. **API funcionando** y accesible
3. **Base de datos MongoDB** lista para recibir datos

## Pasos para Ejecutar

### 1. Importar la Colección

1. Abre Postman
2. Haz clic en **Import** (botón superior izquierdo)
3. Selecciona el archivo `Postman-Collection-LoadTest.json`
4. La colección aparecerá en tu workspace

### 2. Configurar la Variable de Entorno

1. Selecciona la colección **"Load Test - Complete Data Generation"**
2. Ve a la pestaña **Variables**
3. Verifica que `baseUrl` esté configurado correctamente:
   - Para local: `http://localhost:8080`
   - Para AWS: `http://franchise-alb-993974778.us-east-1.elb.amazonaws.com`
   - O la URL de tu API

### 3. Ejecutar el Collection Runner

1. Haz clic en la colección
2. Haz clic en **Run** (botón superior derecho) o presiona `Ctrl+Alt+R` (Windows/Linux) o `Cmd+Alt+R` (Mac)
3. En la ventana del Collection Runner:
   - **Iterations**: Configura `100` (una iteración por franquicia)
   - **Delay**: Opcional, puedes agregar un delay entre requests (recomendado: 100-500ms)
   - **Data**: No es necesario, la colección genera los datos automáticamente
4. Haz clic en **Run Load Test**

### 4. Monitorear el Progreso

- Observa la consola de Postman para ver el progreso
- Verás logs como:
  ```
  === Iteración 1/100 ===
  Creando franquicia: Franquicia 001 con 15 sucursales
  ✓ Franquicia creada con ID: 1
  
    [1/15] Creando sucursal: Sucursal Bogotá 1 con 12 productos
    ✓ Sucursal creada con ID: 1
      [1/12] Producto: Laptop Dell XPS 13 (Stock: 245)
      ...
  ```

## Estructura de la Colección

La colección tiene 3 requests principales que se ejecutan en secuencia:

1. **1. Create Franchise**: Crea una franquicia y determina cuántas sucursales crear (10-20)
2. **2. Create Branch**: Crea una sucursal y determina cuántos productos crear (10-20)
3. **3. Create Product**: Crea productos hasta completar la cantidad determinada

Los scripts controlan el flujo usando `postman.setNextRequest()` para:
- Repetir la creación de productos hasta completar la cantidad
- Repetir la creación de sucursales hasta completar la cantidad
- Continuar con la siguiente franquicia en la siguiente iteración

## Datos Generados

### Franquicias
- Nombre: `Franquicia 001`, `Franquicia 002`, ..., `Franquicia 100`
- Descripción: `Descripción de la franquicia {número}`

### Sucursales
- Nombre: `Sucursal {Ciudad} {número}`
- Ciudades aleatorias: Bogotá, Medellín, Cali, Barranquilla, Cartagena, Bucaramanga, Pereira, Santa Marta, Manizales, Armenia
- Dirección: `Calle {número} #{número}-{número}` (aleatorio)

### Productos
- Pool de 40 productos tecnológicos diferentes
- Los productos pueden repetirse entre sucursales (realista)
- Stock aleatorio entre 10-500 unidades

## Tiempo Estimado

Dependiendo de la velocidad de tu API y el delay configurado:
- **Sin delay**: ~15-30 minutos
- **Con delay de 100ms**: ~30-60 minutos
- **Con delay de 500ms**: ~1-2 horas

## Verificación Post-Ejecución

Después de ejecutar la colección, puedes verificar los datos:

```bash
# Obtener todas las franquicias
curl http://localhost:8080/api/franchises

# Obtener una franquicia específica con sus sucursales
curl http://localhost:8080/api/franchises/1

# Obtener productos de una sucursal
curl http://localhost:8080/api/franchises/1/branches/1/products
```

## Solución de Problemas

### La colección se detiene antes de completar
- Verifica que la API esté funcionando correctamente
- Revisa los logs de la consola de Postman para ver errores
- Asegúrate de que MongoDB esté accesible

### Errores de conexión
- Verifica que `baseUrl` esté configurado correctamente
- Verifica que la API esté corriendo y accesible
- Si usas AWS, verifica que el ALB esté saludable

### Timeouts
- Aumenta el timeout en Postman: Settings → General → Request timeout
- Considera agregar un delay entre requests para no saturar la API

## Notas Importantes

⚠️ **Esta colección borrará y recreará datos**. Si ya tienes datos en la base de datos, considera:
- Hacer un backup antes de ejecutar
- O modificar los nombres para evitar conflictos
- O borrar los datos existentes primero

💡 **Para pruebas más realistas**, puedes:
- Modificar el pool de productos en la variable `productNamesPool`
- Ajustar los rangos de sucursales y productos
- Agregar más ciudades o datos aleatorios
