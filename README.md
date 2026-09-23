# TaskBoard API

![Java](https://img.shields.io/badge/Java-17-orange?style=for-the-badge&logo=openjdk)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.x-6DB33F?style=for-the-badge&logo=springboot)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-17-4169E1?style=for-the-badge&logo=postgresql)
![Docker](https://img.shields.io/badge/Docker-ready-2496ED?style=for-the-badge&logo=docker)

A RESTful Task Management API built with **Java** and **Spring Boot**, designed with clean architecture principles, DTO-based contracts, centralized exception handling, and unit test coverage.

This project was built as a backend portfolio piece to demonstrate REST API design, layered architecture, and testing practices with the Spring ecosystem.

## 🚀 Live Demo

**Deployed on Render + Supabase PostgreSQL:**

- 🔗 **Swagger UI:** [https://taskboard-api-6aml.onrender.com/swagger-ui/index.html](https://taskboard-api-6aml.onrender.com/swagger-ui/index.html)
- 🔗 **API Base URL:** [https://taskboard-api-6aml.onrender.com/api/tasks](https://taskboard-api-6aml.onrender.com/api/tasks)

> ⚠️ The free-tier instance sleeps after 15 minutes of inactivity. The first request may take up to 50 seconds to wake it up.

## Features

- Full CRUD for tasks (create, read, update, delete)
- Filtering tasks by status (`GET /api/tasks?status=PENDING`)
- Pagination and sorting for the listing endpoint (`GET /api/tasks?page=0&size=10&sort=dueDate,asc`)
- Request validation with Bean Validation (`@NotBlank`, `@Size`, `@NotNull`, `@FutureOrPresent`)
- Standardized error responses following **RFC 7807 (ProblemDetail)**
- Interactive API documentation with **Swagger / OpenAPI 3**
- DTO-based request/response contracts — the JPA entity is never exposed directly through the API
- Unit tests for the service and controller layers using **JUnit 5**, **Mockito**, and **MockMvc**
- Dockerized for consistent deployment

## Tech Stack

| Category            | Technology                          |
|---------------------|--------------------------------------|
| Language            | Java 17                              |
| Framework           | Spring Boot                          |
| Persistence         | Spring Data JPA + PostgreSQL         |
| Database (prod)     | Supabase (managed PostgreSQL)        |
| Validation          | Jakarta Bean Validation              |
| API Documentation   | springdoc-openapi (Swagger UI)       |
| Testing             | JUnit 5, Mockito, MockMvc            |
| Build Tool          | Maven                                |
| Containerization    | Docker (multi-stage build)           |
| Deployment          | Render                               |

## Architecture

The project follows a **layered architecture**:

```
Controller  →  Service  →  Repository  →  Database
    ↑              ↓
  DTOs      Entity (Task)
```


- **Controller**: exposes REST endpoints, receives/returns DTOs only
- **Service**: business logic, entity ↔ DTO conversion, exception handling
- **Repository**: data access via Spring Data JPA
- **Entity**: `Task`, mapped to the `tasks` table

## Data Model

**Task**

| Field       | Type            | Notes                              |
|-------------|-----------------|-------------------------------------|
| id          | Long            | Auto-generated                     |
| title       | String          | Required, 3–100 characters         |
| description | String          | Optional, up to 500 characters     |
| status      | TaskStatus      | `PENDING`, `IN_PROGRESS`, `DONE`   |
| priority    | TaskPriority    | `LOW`, `MEDIUM`, `HIGH`            |
| dueDate     | LocalDateTime   | Must be present or future          |
| createdAt   | LocalDateTime   | Set automatically on creation      |
| updatedAt   | LocalDateTime   | Set automatically on update        |

## API Endpoints

| Method | Endpoint              | Description                       |
|--------|------------------------|------------------------------------|
| GET    | `/api/tasks`           | List tasks (paginated, optional `status` filter) |
| GET    | `/api/tasks/{id}`      | Get a task by ID                   |
| POST   | `/api/tasks`           | Create a new task                  |
| PUT    | `/api/tasks/{id}`      | Update an existing task            |
| DELETE | `/api/tasks/{id}`      | Delete a task                      |
| GET    | `/api/health`          | Health check endpoint              |

### Example error response (400 Bad Request)

```json
{
  "type": "https://api.taskboard.com/errors/validation-error",
  "title": "Invalid Request Content",
  "status": 400,
  "detail": "Validation failed for one or more fields",
  "invalidFields": {
    "title": "Title is required"
  }
}
```

## Getting Started

### Prerequisites

- Java 17+
- Maven (or use the included `./mvnw` wrapper)
- MySQL running locally

### 1. Clone the repository

```bash
git clone https://github.com/DanniaLima/taskboard-api.git
cd taskboard-api
```

### 2. Create the database

```sql
CREATE DATABASE taskboard_db;
```

### 3. Configure your local credentials

Create `src/main/resources/application-local.properties`:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/taskboard_db
spring.datasource.username=your_username
spring.datasource.password=your_password
```
This file is gitignored — your credentials never leave your machine.

### 4. Run the application

```bash
./mvnw spring-boot:run
```

The API will be available at `http://localhost:8080`.

### 5. Explore the API with Swagger

Once the app is running, open:

```
http://localhost:8080/swagger-ui/index.html
```

## Running Tests

```bash
./mvnw test
```

Tests cover both layers:
- **Service layer** with JUnit 5 + Mockito (business logic, entity-to-DTO conversion, exception handling, repository interactions)
- **Controller layer** with MockMvc (HTTP status codes, JSON contracts, validation errors, exception → status mapping)

## Deployment

The API is deployed on Render using a multi-stage Dockerfile and connects to a Supabase PostgreSQL instance.

- **Build:** Docker multi-stage (`eclipse-temurin:17-jdk-alpine` → `eclipse-temurin:17-jre-alpine`)
- **Profiles:** `local` (dev) and `prod` (deployment) — credentials loaded via environment variables
- **CI/CD:** Automatic redeploy on every push to `main`

## Project Structure

```
taskboard-api/
├── src/
│   ├── main/
│   │   ├── java/com/dannialima/taskboard/
│   │   │   ├── controller/
│   │   │   ├── dto/
│   │   │   ├── exception/
│   │   │   ├── model/
│   │   │   ├── repository/
│   │   │   └── service/
│   │   └── resources/
│   │       ├── application.properties
│   │       ├── application-local.properties (gitignored)
│   │       └── application-prod.properties
│   └── test/
│       └── java/com/dannialima/taskboard/
│           ├── controller/
│           ├── service/
│           └── TaskboardApiApplicationTests.java
├── Dockerfile
├── pom.xml
└── README.md
```

## Roadmap

- [x] CRUD endpoints
- [x] Bean Validation
- [x] Global exception handling (RFC 7807)
- [x] Swagger / OpenAPI documentation
- [x] DTO-based contracts (no entity exposure)
- [x] Unit tests for the service layer
- [x] Controller layer tests with MockMvc
- [x] Pagination and sorting for the listing endpoint
- [x] Dockerization
- [x] Cloud deployment (Render + Supabase)

## Author

**Amisterdania (Dania) de Oliveira Lima**
AI Developer & Data Analyst student — transitioning into Full-Stack & Backend development.

- GitHub: [@DanniaLima](https://github.com/DanniaLima)
