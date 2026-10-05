# Producto API — Swagger y pruebas con Postman

API REST desarrollada con Spring Boot para gestionar productos mediante operaciones CRUD.

Este proyecto corresponde a la actividad de la **Semana 9 de Desarrollo Fullstack**, enfocada en la documentación de una API REST con Swagger y la validación de endpoints mediante Postman.

## Objetivos

- Documentar la API utilizando Swagger / OpenAPI.
- Verificar el funcionamiento de los endpoints con Postman.
- Realizar al menos tres pruebas.
- Incluir un caso de error `400` o `404`.
- Interpretar los códigos de estado HTTP obtenidos.

## Tecnologías utilizadas

- Java 25
- Spring Boot 4.1.1
- Spring Web
- Spring Data JPA
- Bean Validation
- H2 Database
- Maven
- Springdoc OpenAPI
- Swagger UI
- Postman

## Estructura del proyecto

```text
src/main/java/com/fullstack/productoapi
│
├── controller
│   └── ProductoController.java
│
├── entity
│   └── Producto.java
│
├── exception
│   ├── GlobalExceptionHandler.java
│   └── ResourceNotFoundException.java
│
├── repository
│   └── ProductoRepository.java
│
├── service
│   └── ProductoService.java
│
└── ProductoApiApplication.java
```

## Arquitectura

El proyecto utiliza una arquitectura por capas:

### Entity

La entidad `Producto` representa los datos almacenados en la aplicación.

Campos:

- `id`
- `nombre`
- `descripcion`
- `precio`
- `stock`

### Repository

`ProductoRepository` utiliza `JpaRepository` para acceder a la base de datos y realizar operaciones CRUD.

### Service

`ProductoService` contiene la lógica para:

- Crear productos.
- Listar productos.
- Consultar productos por ID.
- Actualizar productos.
- Eliminar productos.

### Controller

`ProductoController` expone los endpoints REST de la aplicación y contiene anotaciones de OpenAPI para documentarlos en Swagger.

## Base URL

```text
http://localhost:8080/api/productos
```

## Endpoints

| Método | Endpoint | Descripción | Código esperado |
|---|---|---|---|
| POST | `/api/productos` | Crear un producto | 201 Created |
| GET | `/api/productos` | Listar todos los productos | 200 OK |
| GET | `/api/productos/{id}` | Obtener producto por ID | 200 OK |
| PUT | `/api/productos/{id}` | Actualizar un producto | 200 OK |
| DELETE | `/api/productos/{id}` | Eliminar un producto | 204 No Content |

## Swagger / OpenAPI

Para documentar la API se utilizó Springdoc OpenAPI.

La interfaz gráfica de Swagger está disponible en:

```text
http://localhost:8080/swagger-ui.html
```

La especificación OpenAPI en formato JSON está disponible en:

```text
http://localhost:8080/v3/api-docs
```

Desde Swagger es posible visualizar y probar los endpoints del recurso `Producto`.

## Pruebas realizadas con Postman

Se realizaron las siguientes pruebas:

| Prueba | Método | Endpoint | Resultado |
|---|---|---|---|
| Crear producto | POST | `/api/productos` | 201 Created |
| Listar productos | GET | `/api/productos` | 200 OK |
| Producto inexistente | GET | `/api/productos/999999` | 404 Not Found |
| Datos inválidos | POST | `/api/productos` | 400 Bad Request |

## Prueba 1 — Crear producto

### Request

```http
POST /api/productos
```

Body:

```json
{
  "nombre": "Mouse inalámbrico",
  "descripcion": "Mouse ergonómico para computador",
  "precio": 85000,
  "stock": 20
}
```

Resultado:

```text
201 Created
```

Este código indica que el recurso fue creado correctamente.

## Prueba 2 — Listar productos

### Request

```http
GET /api/productos
```

Resultado:

```text
200 OK
```

Este código indica que la solicitud se procesó correctamente y el servidor devolvió la lista de productos.

## Prueba 3 — Producto inexistente

### Request

```http
GET /api/productos/999999
```

Resultado:

```text
404 Not Found
```

Ejemplo de respuesta:

```json
{
  "error": "Not Found",
  "mensaje": "Producto con id 999999 no encontrado"
}
```

Este código indica que el recurso solicitado no existe.

## Prueba adicional — Error de validación

También se realizó una prueba enviando datos incorrectos.

### Request

```http
POST /api/productos
```

Body:

```json
{
  "nombre": "",
  "descripcion": "Producto inválido",
  "precio": -5000,
  "stock": -3
}
```

Resultado:

```text
400 Bad Request
```

Este código indica que los datos enviados por el cliente no cumplen las reglas de validación de la API.

## Interpretación de códigos HTTP

### 200 OK

La solicitud fue procesada correctamente.

Se utiliza, por ejemplo, al consultar o actualizar un producto.

### 201 Created

Indica que un nuevo recurso fue creado correctamente.

Se obtiene al ejecutar:

```text
POST /api/productos
```

### 204 No Content

La operación fue realizada correctamente, pero el servidor no necesita devolver contenido.

Se utiliza al eliminar un producto.

### 400 Bad Request

La solicitud contiene datos incorrectos o no cumple las reglas de validación.

Ejemplos:

- Nombre vacío.
- Precio negativo.
- Stock negativo.

### 404 Not Found

El recurso solicitado no existe.

Por ejemplo:

```text
GET /api/productos/999999
```

## Validaciones

La entidad `Producto` contiene reglas de validación para evitar datos incorrectos:

- El nombre no puede estar vacío.
- El precio es obligatorio.
- El precio debe ser mayor que cero.
- El stock es obligatorio.
- El stock no puede ser negativo.

## Evidencias

Las capturas de las pruebas se encuentran dentro de:

```text
evidence/
```

Archivos utilizados:

```text
01-swagger-ui.png
02-postman-create-201.png
03-postman-list-200.png
04-postman-error-404.png
05-postman-error-400.png
```

## Ejecutar el proyecto

Desde la raíz del proyecto:

```bash
./mvnw spring-boot:run
```

En Windows PowerShell:

```powershell
.\mvnw.cmd spring-boot:run
```

La aplicación estará disponible en:

```text
http://localhost:8080
```

## Base de datos

El proyecto utiliza H2 en memoria.

La consola está disponible en:

```text
http://localhost:8080/h2-console
```

Configuración:

```text
JDBC URL: jdbc:h2:mem:productosdb
User Name: sa
Password: vacío
```

## Conclusión

La actividad permitió documentar una API REST utilizando Swagger / OpenAPI y comprobar su funcionamiento mediante Postman. También se verificaron diferentes códigos HTTP, incluyendo respuestas exitosas y casos de error como `400 Bad Request` y `404 Not Found`.