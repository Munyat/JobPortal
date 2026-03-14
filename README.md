# JobPortal — Spring Boot Backend

A lightweight, extensible Job Portal backend implemented with Spring Boot. Intended as a learning project and reference for building production-grade REST APIs with layered architecture, DTOs, validation, security, persistence and tests.

---

Table of contents

- Project summary
- Key features
- Architecture & package layout
- Tech stack & versions
- Prerequisites
- Quick start (local)
- Configuration & environment variables
- Database & migrations
- Docker
- API reference (examples)
- Security (JWT & roles)
- File uploads (resumes)
- Pagination, filtering & sorting
- Testing
- Logging, metrics & monitoring
- CI / Release recommendations
- Development & contribution guide
- Troubleshooting
- License & contact

---

## Project summary

This service provides REST endpoints for:

- User registration, authentication and role management (ADMIN / EMPLOYER / CANDIDATE)
- Job posting CRUD and search/filtering
- Job application flow (apply with resume, view status)
- Employer/candidate views for applications
- Validation, pagination and role-based authorization

## Key features

- Role-based access control (ADMIN, EMPLOYER, CANDIDATE)
- CRUD for jobs with search (title, skills, location, job type, salary range)
- Candidate apply workflow with resume upload
- DTOs, service layer, repository layer (Spring Data JPA)
- Validation (jakarta/javax.validation)
- Unit & integration tests (H2 or Testcontainers)
- Configurable via Spring profiles

## Architecture & package layout

Conventional package layout (adjust to actual package names in project):

- src/main/java/com/yourorg/jobportal
  - controller — REST controllers and DTOs
  - service — business logic, transactions
  - repository — Spring Data JPA interfaces
  - model/entity — JPA entities
  - dto — request/response DTOs and mappers
  - security — JWT filters, auth providers, role config
  - config — beans and configuration classes
- src/main/resources
  - application.yml / application-\*.yml
  - db/migration (if Flyway/Liquibase)

## Tech stack & versions

- Java 17+ (see pom.xml)
- Spring Boot (see pom.xml)
- Spring Web, Spring Data JPA, Spring Security, Validation
- H2 for dev/tests, PostgreSQL recommended for production
- Maven (./mvnw)
- Optional: Flyway or Liquibase for DB migrations

## Prerequisites

- Java 17+
- Maven (or use bundled ./mvnw)
- PostgreSQL (optional; H2 available for quick runs)
- Docker & Docker Compose (optional)

## Quick start (local)

1. Clone:
   git clone <repo-url>
   cd jobportal

2. Build:
   ./mvnw clean package

3. Run (dev/H2 default):
   ./mvnw spring-boot:run
   or
   java -jar target/\*.jar

4. Run tests:
   ./mvnw test

5. API docs (if enabled): http://localhost:8080/swagger-ui.html or /swagger-ui/index.html

## Configuration & environment variables

Primary configuration lives in application.yml / application-\*.yml. Common env vars:

- SPRING_DATASOURCE_URL (e.g. jdbc:postgresql://localhost:5432/jobportal)
- SPRING_DATASOURCE_USERNAME
- SPRING_DATASOURCE_PASSWORD
- SPRING_JPA_HIBERNATE_DDL_AUTO (validate | update | none)
- APP_JWT_SECRET (JWT signing secret)
- APP_JWT_EXP_MS (token expiry in ms)
- APP_UPLOAD_RESUME_DIR (filesystem path for resume uploads)
- SPRING_PROFILES_ACTIVE (dev | test | prod)

Example minimal env for dev (H2):
SPRING_DATASOURCE_URL=jdbc:h2:mem:jobportal;DB_CLOSE_DELAY=-1
APP_JWT_SECRET=change-me

## Database & migrations

- If Flyway/Liquibase is present, migrations live under src/main/resources/db/migration (Flyway) or db/changelog (Liquibase).
- For local dev H2 is convenient; for production use PostgreSQL and run migrations.
- Flyway runs automatically at startup if present and enabled.

## Docker

If Dockerfile / docker-compose.yml exist, basic usage:

- Build image:
  docker build -t jobportal .
- Run with DB:
  docker compose up --build

Set env vars in compose file or pass at runtime.

## API reference (common endpoints & examples)

Note: verify actual routes and DTOs in src/main/java/\*\*/controller.

Authentication

- POST /api/auth/register
  Request JSON: { "email": "...", "password": "...", "role": "CANDIDATE" }
  Response: 201 Created (user summary)

- POST /api/auth/login
  Request JSON: { "email": "...", "password": "..." }
  Response JSON: { "token": "eyJ..." }

Example login:
curl -s -X POST http://localhost:8080/api/auth/login \
 -H "Content-Type: application/json" \
 -d '{"email":"user@example.com","password":"pass"}'

Jobs

- GET /api/jobs
  Query params: q, location, skills (comma-separated), type, page, size, sort
  Response: paginated job DTOs

- POST /api/jobs (EMPLOYER)
  Request JSON: { "title","description","skills":[...],"location","salaryFrom","salaryTo","jobType" }

- GET /api/jobs/{id}
  Returns job details

- PUT /api/jobs/{id} (EMPLOYER owner or ADMIN)
- DELETE /api/jobs/{id} (EMPLOYER owner or ADMIN)

Applications

- POST /api/jobs/{id}/apply (CANDIDATE)
  Multipart/form-data: resume=@/path/resume.pdf, coverLetter="..."
  Example:
  curl -X POST "http://localhost:8080/api/jobs/123/apply" \
   -H "Authorization: Bearer <token>" \
   -F "resume=@/path/to/resume.pdf" \
   -F "coverLetter=I am interested..."

- GET /api/applications
  Role-behavior: employer sees applications for their jobs; candidate sees own applications.

Pagination & sorting

- Standard Spring Pageable query params:
  page (0-based), size, sort (e.g., sort=postedAt,desc)

## Security (JWT & roles)

- Token-based authentication with JWT (Authorization: Bearer <token>).
- Roles enforced via annotations (@PreAuthorize / @RolesAllowed) or security config.
- Store APP_JWT_SECRET securely (env or secrets manager) in production.
- Rotate secrets periodically and ensure token expiry is reasonable.

## File uploads (resumes)

- Resume storage configurable via APP_UPLOAD_RESUME_DIR.
- Validate file type (PDF, DOCX) and size server-side.
- Store metadata in DB and file on filesystem or object storage (S3) if integrated.
- Serve files through protected endpoints that validate requester permissions.

## Testing

- Unit tests: ./mvnw test
- Integration tests: use H2 or Testcontainers for realistic DB tests.
- To run with test profile:
  ./mvnw test -Dspring.profiles.active=test

## Logging, metrics & monitoring

- Spring Boot logging (Logback) configured in application.yml.
- Add spring-boot-starter-actuator for health and metrics.
- Secure actuator endpoints in production profiles.

## CI / Release recommendations

- Add CI pipeline to:
  - Run mvn -T 1C clean verify
  - Static analysis (SpotBugs / Checkstyle / PMD)
  - Run tests and publish artifacts for tags
- Build and push Docker images on release tags

## Development & contribution guide

- Branching: feature/_, fix/_, hotfix/\*
- Commits: follow Conventional Commits (feat, fix, docs, chore)
- Write tests for new features and bug fixes
- Keep controllers thin; business logic in services; transactions at service layer
- Use DTOs for controller inputs/outputs; avoid returning JPA entities directly
- Add/update migration scripts for schema changes

## Troubleshooting (common issues)

- Application fails to start: check logs for DB URL, missing env vars, or port conflicts
- 401 Unauthorized: confirm Authorization header and token validity
- File upload fails: verify upload directory exists and permissions
- Migrations not applied: check Flyway/Liquibase config and resource path

## Useful commands

- Show current dir: pwd
- List files: ls -la
- Build & run tests: ./mvnw clean package && ./mvnw test
- Run app: ./mvnw spring-boot:run
- Run with specific profile: ./mvnw spring-boot:run -Dspring-boot.run.profiles=dev

## License

Add a LICENSE file (e.g., MIT) and specify the license here.

## Contact / Issues

Open issues on the repository for bugs or feature requests. Include logs, reproduction steps and relevant API requests/responses.

---

Notes

- Inspect pom.xml for exact dependency versions and modules.
- Inspect controller packages for the definitive list of endpoints and DTO shapes.
- Before deploying to production, review security settings, secrets management and persistence strategy.
