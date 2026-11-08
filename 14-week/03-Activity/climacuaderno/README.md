# ClimaCuaderno — Secure Fullstack Application

**Course:** Complementaria III — Fullstack
**Assessment:** Corte 3 — Week 14
**Project:** ClimaCuaderno
**Version:** 1.0.0
**Project location:** `14-week/03-Activity/climacuaderno`

---

## 1. Project Overview

ClimaCuaderno is a fullstack web application designed to manage personal temperature and humidity observations.

The application allows users to create accounts, authenticate securely, register environmental measurements, and review their personal records through a responsive dashboard.

Each account has private access to its information. The backend validates authentication and ensures users can only manage their own measurements.

The project demonstrates secure communication between a Java REST API and a web interface, automated testing, database persistence, and containerized deployment.

### Main Features

- User registration and authentication.
- JWT-based session authentication.
- Password encryption using BCrypt hashing.
- Protected REST API endpoints.
- Personal environmental measurement management.
- Temperature and humidity validation.
- Private records associated with authenticated users.
- Responsive interface for desktop and mobile devices.
- Error handling for invalid credentials and failed requests.
- Docker deployment with persistent local storage.

---

## 2. Technology Stack

| Component | Technology |
|---|---|
| Programming language | Java 17 |
| Backend framework | Spring Boot 3.5.7 |
| Security | Spring Security |
| Authentication | JSON Web Tokens (JWT) |
| Password protection | BCrypt |
| Database | H2 Database |
| Persistence | Spring JDBC |
| Frontend | HTML5, CSS3, JavaScript |
| Backend testing | JUnit 5, MockMvc |
| Frontend testing | Node.js Test Runner |
| Build system | Maven |
| Deployment | Docker |
| API architecture | REST |

---

## 3. Application Architecture

The application follows a client-server architecture.

The frontend provides the user interface and sends HTTP requests to the backend.

The Spring Boot API processes authentication, validates requests, applies authorization rules, and communicates with the H2 database.

### Authentication Flow

1. A user creates an account with an email address and password.
2. The backend validates the submitted information.
3. The password is hashed using BCrypt before storage.
4. The user signs in using registered credentials.
5. The backend verifies the credentials and generates a signed JWT.
6. The frontend receives the token and stores it in memory.
7. Protected requests include the token in the Authorization header.
8. Spring Security validates the token before granting access.
9. Users can only access measurements associated with their accounts.

### Project Structure

```text
climacuaderno/
├── Dockerfile
├── .dockerignore
├── .env.example
├── .gitignore
├── pom.xml
├── README.md
├── evidence/
├── frontend/
│   ├── index.html
│   ├── styles.css
│   ├── api.mjs
│   ├── main.mjs
│   └── tests/
│       └── api.test.mjs
└── src/
    ├── main/
    │   ├── java/
    │   │   └── co/edu/corhuila/climacuaderno/
    │   │       ├── auth/
    │   │       ├── common/
    │   │       ├── config/
    │   │       ├── readings/
    │   │       └── users/
    │   └── resources/
    │       ├── application.properties
    │       └── schema.sql
    └── test/
        └── java/
            └── co/edu/corhuila/climacuaderno/
```

---

## 4. Security & run

ClimaCuaderno implements authentication and authorization using Spring Security and JSON Web Tokens. Users must register and sign in before accessing protected resources. Passwords are securely hashed with BCrypt, preventing the application from storing plaintext passwords. After successful authentication, the backend generates an HS256-signed JWT with a limited expiration time of 30 minutes. The frontend sends this token through the Authorization Bearer header when requesting protected information.

The backend validates the token and identifies the authenticated user before returning private records. Requests without valid authentication are rejected with HTTP 401 Unauthorized. Invalid input is handled through validation responses, while duplicate registration attempts are rejected with HTTP 409 Conflict. CORS is configured to control which origins are authorized to interact with the API from a browser.

The application includes automated backend and frontend tests covering authentication, access control, input validation, and error handling. Docker provides a reproducible runtime environment for building and executing the Java application. Environment variables are used to configure sensitive settings, including the JWT signing secret.

The application was tested locally through Docker, and the authentication interface, protected API endpoints, and automated tests were verified during development.

### Security Considerations

- Passwords are hashed using BCrypt.
- JWTs are digitally signed and have a limited lifetime.
- Protected endpoints require authentication.
- User measurements are isolated by account.
- Sensitive configuration is provided through environment variables.
- Error responses do not expose passwords.
- The frontend keeps the access token in memory.
- API requests use standardized HTTP status codes.

JWT payloads are signed but not encrypted. Sensitive information must not be placed inside token claims.

HTTPS, rate limiting, additional monitoring, and production-grade database configuration would be required for a public deployment.

---

## 5. API Endpoints

| Method | Endpoint | Authentication | Description |
|---|---|---|---|
| GET | `/api/health` | Public | Verify API availability |
| POST | `/api/auth/register` | Public | Register a new account |
| POST | `/api/auth/login` | Public | Authenticate and receive JWT |
| GET | `/api/me` | JWT required | Retrieve authenticated user |
| GET | `/api/readings` | JWT required | Retrieve personal measurements |
| POST | `/api/readings` | JWT required | Register a new measurement |
| DELETE | `/api/readings/{id}` | JWT required | Delete a personal measurement |

### Example Measurement

```json
{
  "zone_name": "Study room",
  "temperature": 28,
  "humidity": 65
}
```

The backend validates the submitted information and associates the observation with the authenticated account.

---

## 6. Docker Deployment

### Requirements

- Docker Desktop
- Docker Engine running
- PowerShell or a compatible terminal

### Build the Docker Image

Open a terminal inside the project directory and execute:

```powershell
docker build -t climacuaderno-java:1.0.0 .
```

### Configure JWT Secret

Generate a random signing secret:

```powershell
$bytes = New-Object byte[] 48
$rng = [System.Security.Cryptography.RandomNumberGenerator]::Create()
$rng.GetBytes($bytes)
$rng.Dispose()
$env:JWT_SECRET = [Convert]::ToBase64String($bytes)
```

### Run the Application

```powershell
docker run --rm --name climacuaderno-java -p 8082:8080 -e JWT_SECRET -v climacuaderno-java-data:/app/data climacuaderno-java:1.0.0
```

### Access the Application

Open the following address in a web browser:

**http://localhost:8082/**

The application allows users to register an account, sign in, and manage their personal observations.

### Verify the Container

```powershell
docker ps
```

The container should appear with an active running status.

The database is stored in a named Docker volume to preserve information between container executions.

---

## 7. Automated Testing

Automated testing validates the functionality and reliability of the application.

### Backend Tests

The backend uses JUnit 5 and Spring MockMvc to test authentication, access control, API responses, and validation behavior.

The tests can be executed using Maven inside Docker without installing Maven globally.

```powershell
docker run --rm -v "${PWD}:/workspace" -v climacuaderno-m2:/root/.m2 -w /workspace maven:3.9.11-eclipse-temurin-17 mvn -B -ntp test
```

**Test Results:**

| Metric | Result |
|---|---|
| Executed tests | 8 |
| Failures | 0 |
| Errors | 0 |
| Build status | SUCCESS |

### Frontend Tests

The frontend uses the Node.js built-in test runner to verify HTTP request handling and error responses.

```powershell
node --test frontend/tests/api.test.mjs
```

**Test Results:**

| Metric | Result |
|---|---|
| Executed tests | 4 |
| Passed tests | 4 |
| Failed tests | 0 |

### Overall Testing Summary

A total of **12 automated tests** were executed successfully.

The tests cover normal application behavior as well as error conditions, including rejected authentication and failed HTTP requests.

---

## 8. Authentication and Security Verification

### Successful Authentication

A user registers with a valid email and password, then signs in through the web interface.

After authentication, the application provides access to the private dashboard.

### Measurement Registration

An authenticated user registers a temperature and humidity observation.

The application sends the request to the protected backend API and displays the saved record.

### Invalid Authentication

An attempt to sign in with an incorrect password must be rejected.

The application displays an authentication error instead of granting access to protected information.

### Unauthorized API Request

The following command verifies that the user endpoint requires authentication:

```powershell
curl.exe -i http://localhost:8082/api/me
```

Expected response:

```text
HTTP/1.1 401
```

### CORS Verification

The application serves its frontend and API from the same origin by default.

For cross-origin development, the configured allowed origin can be tested with:

```powershell
curl.exe -i -X OPTIONS "http://localhost:8082/api/readings" -H "Origin: http://localhost:5500" -H "Access-Control-Request-Method: GET" -H "Access-Control-Request-Headers: authorization"
```

The response should contain the appropriate `Access-Control-Allow-Origin` header for a permitted origin.

---

## 9. Verification Evidence

The `evidence` directory contains screenshots collected during local execution and testing.

The evidence documents:

- Successful user authentication.
- Access to the private application dashboard.
- Registration and visualization of measurements.
- Rejection of invalid credentials.
- Protection of authenticated API endpoints.
- Successful backend automated tests.
- Successful frontend automated tests.
- Execution of the application inside Docker.

All screenshots are stored as part of the project repository.

---

## 10. Environment Configuration

The application uses environment variables to manage runtime settings.

| Variable | Purpose |
|---|---|
| `JWT_SECRET` | Secret key used to sign JWT access tokens |
| `CORS_ORIGINS` | Allowed browser origins |
| `DB_JDBC_URL` | Optional H2 database connection URL |

The `.env.example` file provides configuration examples without exposing real credentials.

Sensitive values must never be committed to the repository.

---

## 11. Final Verification

The application was checked using the following procedures:

- Successful compilation and execution of the Java application.
- Successful execution of backend automated tests.
- Successful execution of frontend automated tests.
- Docker image construction and container execution.
- Browser access through the configured application port.
- Registration and authentication of application users.
- Registration and display of protected measurements.
- Validation of unauthorized requests.
- Verification of incorrect authentication behavior.

These checks demonstrate the integration of the frontend, backend, authentication layer, and containerized runtime.

---

## 12. Conclusion

ClimaCuaderno demonstrates the implementation of a fullstack application using Java, Spring Boot, JavaScript, JWT authentication, automated testing, and Docker.

The project combines secure access control with a practical interface for managing private environmental measurements. Its modular architecture separates authentication, application logic, persistence, and frontend interaction.

The automated tests and Docker configuration provide a foundation for repeatable verification and local deployment.

The resulting application satisfies the technical objectives of the Week 14 Fullstack activity.
### Screenshot Gallery

#### Evidence 1

![Evidence 1](evidence/Captura%20de%20pantalla%202026-11-08%20123639.png)

#### Evidence 2

![Evidence 2](evidence/Captura%20de%20pantalla%202026-11-08%20123928.png)

#### Evidence 3

![Evidence 3](evidence/Captura%20de%20pantalla%202026-11-08%20124000.png)

#### Evidence 4

![Evidence 4](evidence/Captura%20de%20pantalla%202026-11-08%20124013.png)

#### Evidence 5

![Evidence 5](evidence/Captura%20de%20pantalla%202026-11-08%20124038.png)

#### Evidence 6

![Evidence 6](evidence/Captura%20de%20pantalla%202026-11-08%20124100.png)

#### Evidence 7

![Evidence 7](evidence/Captura%20de%20pantalla%202026-11-08%20124114.png)