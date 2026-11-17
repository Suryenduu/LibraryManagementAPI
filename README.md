# Library Management REST API

A layered Spring Boot REST API for managing library books, student registrations, and transactional book issue/return workflows backed by MySQL 8.0 and Spring Data JPA.

## Tech Stack
- **Java:** 17
- **Framework:** Spring Boot 4.1.2 (Spring Web, Spring Data JPA, Bean Validation)
- **Database:** MySQL 8.0 (Hibernate 7 ORM)
- **Build Tool:** Maven (`mvnw`)
- **API Testing:** Postman / Thunder Client (`postman/LibraryManagementAPI.postman_collection.json`)

## Project Structure
```text
src/main/java/com/college/library/
├── controller/       # REST Controllers (/api/books, /api/students, /api/issues)
├── dto/              # Request/Response DTOs and standardized ErrorResponse payload
├── entity/           # JPA Entities (Book, Student, BookIssue, IssueStatus)
├── exception/        # Custom exceptions & @RestControllerAdvice global handler
├── repository/       # Spring Data JPA Repositories
└── service/          # Transactional business logic & validation rules