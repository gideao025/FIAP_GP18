# Restaurant Platform API

## 📌 Overview

REST API for managing users in a restaurant platform.

---

## 🚀 Technologies

* Java 21
* Spring Boot
* Spring Data JPA
* PostgreSQL
* Docker & Docker Compose
* Swagger (OpenAPI)

---

## ▶️ How to Run

### Using Docker (recommended)

```bash
docker compose up --build
```

---

## 🌐 Access

* API: http://localhost:8080
* Swagger UI: http://localhost:8080/swagger-ui/index.html

---

## 🧪 Example User

```json
{
  "name": "Victor Mazzola",
  "email": "victor.mazzola@email.com",
  "login": "victormazzola",
  "password": "123456",
  "address": "Rua Mazzola, 100",
  "userType": "CLIENT"
}
```

---

## 📚 Endpoints

| Method | Endpoint                    | Description     |
| ------ | --------------------------- | --------------- |
| POST   | /api/v1/users               | Create user     |
| GET    | /api/v1/users               | Get all users   |
| GET    | /api/v1/users/{id}          | Get user by ID  |
| GET    | /api/v1/users/search?name=  | Search users    |
| PUT    | /api/v1/users/{id}          | Update user     |
| PATCH  | /api/v1/users/{id}/password | Update password |
| DELETE | /api/v1/users/{id}          | Delete user     |
| POST   | /api/v1/users/login         | Validate login  |

---

## 🧱 Architecture

The application follows a layered architecture:

* **Controller** → Handles HTTP requests
* **Service** → Business logic
* **Repository** → Data access
* **DTO** → Data transfer objects

---

## ⚠️ Error Handling

The API uses **ProblemDetail (RFC 7807)** format:

```json
{
  "title": "Resource not found",
  "status": 404,
  "detail": "User not found"
}
```

---

## 🐳 Docker Setup

The application runs using:

* **PostgreSQL container**
* **Spring Boot container**

Docker handles:

* database initialization
* application startup
* service communication

---

## 📄 Documentation

Swagger UI provides interactive API documentation:

http://localhost:8080/swagger-ui/index.html

---

## ✅ Features

* Full CRUD operations
* User authentication (login validation)
* Password update endpoint
* Search by name
* Global exception handling
* DTO pattern (no password exposure)
* Dockerized environment

---

## 🎯 Conclusion

This project demonstrates a complete backend API using modern best practices, including clean architecture, proper error handling, documentation, and containerization.
