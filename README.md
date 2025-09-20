# Medical API

A Spring Boot REST API for managing patients, doctors, and visits in a small medical practice.

It provides:
- Patient listing with optional search, pagination, and filtering by doctor.
- For each patient, the most recent visit per doctor, including doctor stats.
- Creating new visits with proper timezone handling (stored in UTC).


## Table of contents
- Overview
- Tech stack
- Getting started
  - Prerequisites
  - Run with Docker (MySQL)
  - Run locally (manual DB config)
- Database & seed data
- API
  - GET /api/v1/patients
  - POST /api/v1/visits
- Error format
- Testing
- Notes


## Overview
This service exposes REST endpoints to:
- Retrieve patients with their latest visit(s) per doctor, optionally filtered by doctor(s), with pagination and search by patient name.
- Create a new visit for a patient with an assigned doctor. Visit times are provided in the doctor’s local timezone and are persisted as UTC.

The application initializes schema and seed data at startup using `src/main/resources/data.sql`.


## Tech stack
- Java 21
- Spring Boot 3.5.x (Web, Validation, Data JPA)
- MapStruct for DTO mapping
- MySQL (runtime) / H2 (tests)
- Maven
- Optional: Spring Boot Docker Compose support


## Getting started
### Prerequisites
- JDK 21
- Maven (or the provided wrapper `mvnw` / `mvnw.cmd`)
- Docker (optional, for running MySQL locally via Compose)

### Run with Docker (MySQL)
This repo contains `compose.yaml` with a MySQL service:
- Port: 3306
- DB: `testdb`
- User: `user` / `pass`
- Root password: `root`

Steps:
1) Start MySQL
   - Docker Desktop running
   - In project root: `docker compose up -d`
2) Run the application
   - Option A (Docker Compose integration): simply run the app; Spring Boot can discover the compose service.
     - Windows: `mvnw.cmd spring-boot:run`
     - Linux/macOS: `./mvnw spring-boot:run`
   - Option B (explicit datasource config): run with properties/env matching compose
     - Example properties:
       - `spring.datasource.url=jdbc:mysql://localhost:3306/testdb`
       - `spring.datasource.username=user`
       - `spring.datasource.password=pass`
     - Windows PowerShell example:
       - `$env:SPRING_DATASOURCE_URL='jdbc:mysql://localhost:3306/testdb'`
       - `$env:SPRING_DATASOURCE_USERNAME='user'`
       - `$env:SPRING_DATASOURCE_PASSWORD='pass'`
       - `mvnw.cmd spring-boot:run`

### Run locally (manual DB config)
If you already have a local MySQL instance, configure the same properties (URL/user/password) accordingly and start the app as above.

The application runs DB schema/data initialization on startup via `data.sql`.


## Database & seed data
Tables created at startup (see `src/main/resources/data.sql`):
- patients(id, first_name, last_name)
- doctors(id, first_name, last_name, timezone)
- visits(id, start_datetime, end_datetime, patient_id, doctor_id)

Seed data provides 9 patients, 4 doctors (with timezones), and several visits (timestamps in UTC).


## API
Base URL: `/api/v1`
Content type: `application/json`
# Medical Management System

## API Documentation

📚 **Live API Documentation**: [View on GitHub Pages](https://redlineod.github.io/medical/)

### Quick Links
- 🌐 **Interactive Docs**: [Swagger UI](https://redlineod.github.io/medical/)
- 📄 **OpenAPI Spec (YAML)**: [Download](https://redlineod.github.io/medical/api.yaml)
- 📄 **OpenAPI Spec (JSON)**: [Download](https://redlineod.github.io/medical/api.json)
- ✏️ **Edit in Swagger Editor**: [Import to editor.swagger.io](https://editor.swagger.io/?url=https://redlineod.github.io/medical/api.yaml)

## Local Development


### GET /api/v1/patients
Returns a paged list of patients with their most recent visit per doctor.

Query parameters:
- `page` (integer, default `0`) — zero-based page index
- `size` (integer, default `20`) — page size
- `search` (string, optional) — case-insensitive match by first or last name
- `doctorIds` (list<long>, optional) — filter to patients who have visits with the given doctor(s). Accepts either repeated params (`doctorIds=1&doctorIds=3`) or comma-separated (`doctorIds=1,3`).

Response body:
- `count` — total number of matching patients (ignores pagination)
- `data` — array of patients
  - `firstName`
  - `lastName`
  - `lastVisits` — array (one per doctor found; most recent visit per doctor)
    - `start` — ISO string in UTC, format `yyyy-MM-dd'T'HH:mm:ss`
    - `end` — ISO string in UTC
    - `doctor`
      - `firstName`
      - `lastName`
      - `totalPatients` — how many distinct patients this doctor has overall

Example:
GET `/api/v1/patients?page=0&size=3&search=Kravchuk&doctorIds=3`

Response (example):
```
{
  "count": 2,
  "data": [
    {
      "firstName": "Andriy",
      "lastName": "Kravchuk",
      "lastVisits": [
        {
          "start": "2025-09-25T07:15:00",
          "end": "2025-09-25T07:45:00",
          "doctor": {
            "firstName": "Sofia",
            "lastName": "Melnyk",
            "totalPatients": 3
          }
        }
      ]
    }
  ]
}
```

### POST /api/v1/visits
Creates a new visit. You provide local times in the doctor’s timezone; the service persists UTC equivalents.

Request body:
```
{
  "start": "2025-10-01T10:00:00",  // LocalDateTime in doctor's timezone
  "end": "2025-10-01T10:30:00",
  "doctorId": 1,
  "patientId": 1
}
```

Rules:
- The doctor and patient must exist.
- Overlapping visits for the same doctor are rejected.
- The doctor’s timezone (from Doctor.timezone) is used to convert the provided local start/end to UTC for storage.

Success (201):
```
{
  "id": 42,
  "start": "2025-10-01T10:00:00",
  "end": "2025-10-01T10:30:00"
}
```


## Error format
Validation and domain errors are returned in a consistent JSON shape (see `ApiErrorResponse`):
- HTTP 400 — validation or business rule violations
- HTTP 404 — entity not found (e.g., doctor/patient)

Example 400:
```
{
  "status": 400,
  "message": "Start time is required",
  "errors": ["start: Start time is required"]
}
```


## Testing
- Run all tests: `mvnw.cmd test` (Windows) or `./mvnw test` (Linux/macOS)
- Tests use H2 in-memory DB (MySQL mode) and the same `data.sql` for deterministic results.
- Integration tests:
  - `PatientControllerIT` — listing, pagination, search, and filtering by doctor
  - `VisitControllerIT` — visit creation (timezone handling) and validation errors


## Notes
- Timezones:
  - POST /visits accepts LocalDateTime in doctor’s local timezone; the service converts to UTC for storage.
  - Patient listing currently formats visit times in UTC.
- Pagination is zero-based (`page=0` is the first page).
- Database is initialized from `data.sql` at startup. Use Dockerized MySQL or provide compatible credentials.



## Frontend UI (AI generated!!!)
A modern React + Vite single-page app is included to interact with this API.

Features:
- Patients list with search, pagination and filter by doctor IDs
- Create Visit form (validates inputs and shows API errors)
- Clean UI with Tailwind CSS

Location: `frontend/`

Run locally:
1) Start the backend (default on http://localhost:8080)
   - Windows: `mvnw.cmd spring-boot:run`
2) In another terminal, start the frontend dev server:
   - `cd frontend`
   - `npm install`
   - `npm run dev`
3) Open http://localhost:5173

Notes:
- The dev server is configured to proxy `/api` to `http://localhost:8080` to avoid CORS issues.
- The Patients page accepts comma-separated doctor IDs for filtering (e.g., `3` or `1,3`).
- The Create Visit page expects `datetime-local` values; ensure you enter times in the doctor's timezone (as per the backend rules).
