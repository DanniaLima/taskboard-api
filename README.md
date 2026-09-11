# TaskBoard API

A RESTful Task Management API built with **Java** and **Spring Boot**, designed with clean architecture principles, DTO-based contracts, centralized exception handling, and unit test coverage.

This project was built as a backend portfolio piece to demonstrate REST API design, layered architecture, and testing practices with the Spring ecosystem.

## Features

- Full CRUD for tasks (create, read, update, delete)
- Filtering tasks by status (`GET /api/tasks?status=PENDING`)
- Request validation with Bean Validation (`@NotBlank`, `@Size`, `@NotNull`, `@FutureOrPresent`)
- Standardized error responses following **RFC 7807 (ProblemDetail)**
- Interactive API documentation with **Swagger / OpenAPI 3**
- DTO-based request/response contracts — the JPA entity is never exposed directly through the API
- Unit tests for the service layer using **JUnit 5** and **Mockito**

## Tech Stack

| Category            | Technology                          |
|----------------------|--------------------------------------|
| Language             | Java 17                              |
| Framework            | Spring Boot                          |
| Persistence          | Spring Data JPA + MySQL              |
| Validation           | Jakarta Bean Validation              |
| API Documentation    | springdoc-openapi (Swagger UI)       |
| Testing              | JUnit 5, Mockito                     |
| Build Tool           | Maven                                |

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
| GET    | `/api/tasks`           | List all tasks (optional `status` filter) |
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

### 3. Configure `application.properties`

Edit `src/main/resources/application.properties` with your local MySQL credentials:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/taskboard_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=your_password_here
spring.jpa.hibernate.ddl-auto=update
```

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

Unit tests cover the service layer, including entity-to-DTO conversion, exception handling for missing resources, and repository interaction verification with Mockito.

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
│   │       └── application.properties
│   └── test/
│       └── java/com/dannialima/taskboard/service/
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
- [ ] Controller layer tests with MockMvc
- [ ] Pagination and sorting for the listing endpoint

## Author

**Amisterdania (Dania) de Oliveira Lima**
AI Developer & Data Analyst student — transitioning into Full-Stack & Backend development.

- GitHub: [@DanniaLima](https://github.com/DanniaLima)
