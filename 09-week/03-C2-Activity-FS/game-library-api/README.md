# Game Library API

REST API developed with Spring Boot for managing a video game library.

This project was created as part of the Fullstack Development course. It implements a complete CRUD using a layered architecture, JPA persistence, validation, Swagger documentation, and Postman testing.

## Technologies

- Java 17
- Spring Boot 4.1.1
- Spring Web MVC
- Spring Data JPA
- Bean Validation
- H2 Database
- Maven
- Swagger / OpenAPI
- Postman

## Project structure

The project follows a layered architecture:

```text
src/main/java/com/fullstack/gamelibrary
│
├── controller
│   └── JuegoController.java
│
├── entity
│   └── Juego.java
│
├── exception
│   ├── GlobalExceptionHandler.java
│   └── ResourceNotFoundException.java
│
├── repository
│   └── JuegoRepository.java
│
├── service
│   └── JuegoService.java
│
└── GameLibraryApiApplication.java
```

### Layers

- `entity`: defines the `Juego` entity and its validation rules.
- `repository`: manages database access using Spring Data JPA.
- `service`: contains the application business logic.
- `controller`: exposes the REST API endpoints.
- `exception`: manages errors such as 400 Bad Request and 404 Not Found.

## Requirements

Before running the project, make sure you have:

- Java 17 or higher.
- Maven or the Maven Wrapper included in the project.

You can verify Java with:

```bash
java -version
```

## Running the application

Open a terminal inside the project folder.

On Windows, run:

```bash
.\mvnw.cmd spring-boot:run
```

The application will start at:

```text
http://localhost:8080
```

## API reference

The API exposes five endpoints to manage video games. `GET /api/v1/juegos` returns all games stored in the database. `GET /api/v1/juegos/{id}` returns a specific game using its identifier and produces a 404 response when the game does not exist. `POST /api/v1/juegos` creates a new game and validates the information sent in the request body. `PUT /api/v1/juegos/{id}` updates an existing game using the provided identifier. `DELETE /api/v1/juegos/{id}` removes an existing game from the database. Invalid information sent to the API produces a 400 Bad Request response.

| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/v1/juegos` | Returns all games |
| GET | `/api/v1/juegos/{id}` | Returns a game by ID |
| POST | `/api/v1/juegos` | Creates a new game |
| PUT | `/api/v1/juegos/{id}` | Updates an existing game |
| DELETE | `/api/v1/juegos/{id}` | Deletes an existing game |

## Game model

A game contains the following fields:

| Field | Type | Description |
|---|---|---|
| id | Long | Unique identifier |
| titulo | String | Game title |
| genero | String | Game genre |
| plataforma | String | Game platform |
| precio | BigDecimal | Game price |

## Example request

### Create a game

```http
POST /api/v1/juegos
```

Request body:

```json
{
  "titulo": "Cyberpunk 2077",
  "genero": "RPG",
  "plataforma": "PC",
  "precio": 150000
}
```

Successful response:

```text
201 Created
```

Example response:

```json
{
  "id": 3,
  "titulo": "Cyberpunk 2077",
  "genero": "RPG",
  "plataforma": "PC",
  "precio": 150000
}
```

## Error handling

The application includes centralized exception handling.

### 400 Bad Request

Invalid information produces a `400 Bad Request`.

Example:

```json
{
  "titulo": "",
  "genero": "",
  "plataforma": "PC",
  "precio": -100
}
```

Possible response:

```json
{
  "status": 400,
  "error": "Bad Request",
  "messages": {
    "titulo": "El titulo es obligatorio",
    "genero": "El genero es obligatorio",
    "precio": "El precio no puede ser negativo"
  }
}
```

### 404 Not Found

Requesting a game that does not exist produces a `404 Not Found`.

Example:

```http
GET /api/v1/juegos/999
```

Possible response:

```json
{
  "status": 404,
  "error": "Not Found",
  "message": "Juego no encontrado con id: 999"
}
```

## Swagger

Swagger UI is available at:

```text
http://localhost:8080/swagger-ui.html
```

The OpenAPI specification is available at:

```text
http://localhost:8080/v3/api-docs
```

## Database

The application uses H2 Database with file persistence.

Database URL:

```text
jdbc:h2:file:./data/gamelibrary
```

H2 Console:

```text
http://localhost:8080/h2-console
```

Connection information:

```text
JDBC URL: jdbc:h2:file:./data/gamelibrary
User Name: sa
Password: empty
```

## Tests

The API was tested using Swagger and Postman.

The tests include:

- GET all games.
- GET game by ID.
- POST a new game.
- PUT an existing game.
- DELETE an existing game.
- 400 Bad Request validation.
- 404 Not Found validation.

## Evidence

The `evidence` folder contains screenshots of the API tests performed with Swagger and Postman.

```text
evidence/
├── swagger.png
├── swagger-error-404.png
├── swagger-error-400.png
├── swagger-get-all.png
├── swagger-get-one.png
├── swagger-put.png
├── swagger-delete.png
├── postman-post.png
├── postman-get-one.png
├── postman-put.png
├── postman-delete.png
├── postman-error-404.png
└── postman-error-400.png
```

## Author

Fullstack Development  
Corte 2 - Week 9