# Producto API

API REST desarrollada con Spring Boot para gestionar productos mediante operaciones CRUD.

Este proyecto fue realizado como parte de la actividad de la **Semana 8 de Desarrollo Fullstack**, correspondiente al tema de APIs REST y persistencia.

## Objetivo

Implementar un CRUD REST completo utilizando una arquitectura por capas:

- Entity
- Repository
- Service
- Controller

Además, se probaron los endpoints principales para crear, listar, consultar, actualizar y eliminar productos.

## Tecnologías utilizadas

- Java 17+
- Spring Boot
- Spring Web
- Spring Data JPA
- Bean Validation
- H2 Database
- Maven
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

El proyecto utiliza una arquitectura por capas.

### Entity

Define la estructura del recurso `Producto` y sus validaciones.

Campos principales:

- `id`
- `nombre`
- `descripcion`
- `precio`
- `stock`

### Repository

`ProductoRepository` extiende `JpaRepository` y permite realizar operaciones de persistencia sobre la base de datos.

### Service

`ProductoService` contiene la lógica necesaria para:

- Crear productos.
- Listar productos.
- Buscar un producto por ID.
- Actualizar productos.
- Eliminar productos.

### Controller

`ProductoController` expone los endpoints REST de la aplicación.

## Base URL

```text
http://localhost:8080/api/productos
```

## Endpoints

| Método | Endpoint | Descripción | Código esperado |
|---|---|---|---|
| POST | `/api/productos` | Crear un producto | 201 Created |
| GET | `/api/productos` | Listar todos los productos | 200 OK |
| GET | `/api/productos/{id}` | Obtener un producto por ID | 200 OK |
| PUT | `/api/productos/{id}` | Actualizar un producto | 200 OK |
| DELETE | `/api/productos/{id}` | Eliminar un producto | 204 No Content |

## Ejemplo para crear un producto

### Request

```http
POST /api/productos
```

```json
{
  "nombre": "Teclado mecánico",
  "descripcion": "Teclado RGB para computador",
  "precio": 180000,
  "stock": 10
}
```

### Response

```json
{
  "id": 1,
  "nombre": "Teclado mecánico",
  "descripcion": "Teclado RGB para computador",
  "precio": 180000,
  "stock": 10
}
```

Código HTTP:

```text
201 Created
```

## Validaciones

La entidad `Producto` contiene validaciones para evitar información incorrecta.

- El nombre es obligatorio.
- El precio es obligatorio y debe ser mayor que cero.
- El stock es obligatorio.
- El stock no puede ser negativo.

Ejemplo de petición incorrecta:

```json
{
  "nombre": "",
  "descripcion": "Producto incorrecto",
  "precio": -5000,
  "stock": -3
}
```

La API responde con:

```text
400 Bad Request
```

## Manejo de errores

Cuando se intenta consultar un producto inexistente, por ejemplo:

```http
GET /api/productos/999
```

La API devuelve:

```text
404 Not Found
```

Ejemplo:

```json
{
  "error": "Not Found",
  "mensaje": "Producto con id 999 no encontrado"
}
```

## Pruebas realizadas

Se realizaron pruebas del CRUD completo utilizando Postman.

| Prueba | Método | Resultado |
|---|---|---|
| Crear producto | POST | 201 Created |
| Listar productos | GET | 200 OK |
| Obtener producto | GET | 200 OK |
| Actualizar producto | PUT | 200 OK |
| Eliminar producto | DELETE | 204 No Content |
| Producto inexistente | GET | 404 Not Found |
| Datos inválidos | POST | 400 Bad Request |

## Evidencias

Las capturas de las pruebas realizadas se encuentran en la carpeta:

```text
evidence/
```

Archivos:

```text
01-create-product-201.png
02-list-products-200.png
03-get-product-200.png
04-update-product-200.png
05-delete-product-204.png
06-product-not-found-404.png
07-invalid-product-400.png
```

## Ejecutar el proyecto

Desde la raíz del proyecto ejecutar:

```bash
./mvnw spring-boot:run
```

En Windows PowerShell:

```powershell
.\mvnw.cmd spring-boot:run
```

La aplicación se ejecutará en:

```text
http://localhost:8080
```

## Base de datos

El proyecto utiliza una base de datos H2 en memoria.

La consola de H2 está disponible en:

```text
http://localhost:8080/h2-console
```

Configuración utilizada:

```text
JDBC URL: jdbc:h2:mem:productosdb
User Name: sa
Password: vacío
```

## Conclusión

La actividad permitió implementar un CRUD REST funcional utilizando Spring Boot y una arquitectura por capas. También se realizaron pruebas de extremo a extremo para comprobar el funcionamiento correcto de los métodos HTTP y los códigos de estado de la API.