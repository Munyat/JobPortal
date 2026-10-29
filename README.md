# JobPortal — Spring Boot REST API

JobPortal is a Java backend for a recruitment platform. It models companies, job listings, job seekers, employer accounts, candidate profiles, saved jobs, and job applications. The repository also demonstrates API versioning, JWT request authentication, role-based route rules, auditing, caching, external HTTP clients, and observability integration.

**Repository:** [Munyat/JobPortal](https://github.com/Munyat/JobPortal)

## What the application supports

- User registration and login, with a JWT returned after successful authentication.
- Role-oriented workflows for **job seekers**, **employers**, and **administrators**.
- Company administration and public company listings containing active jobs.
- Employer job creation, listing, and status changes.
- Candidate profile management, including profile-picture and resume uploads.
- Saving jobs, applying for jobs, withdrawing applications, and viewing application history.
- Employer-side application review and status/notes updates.
- Contact-message submission, administration, sorting, pagination, and closure.
- API versioning, request validation, centralized exception responses, configurable CORS, CSRF support, caching, audit fields, and logging/telemetry integration.
- Example /posts and /todos endpoints backed by Spring HTTP service clients configured for JSONPlaceholder.

The feature list reflects the controllers and services present in the source. Authentication behavior differs by Spring profile; see [Security and configuration notes](#security-and-configuration-notes) before using the application beyond local development.

## Technology

| Area | Implementation |
|---|---|
| Language | Java 25 |
| Framework | Spring Boot 4.0.3 |
| Build | Maven and Maven Wrapper |
| HTTP API | Spring MVC |
| Persistence | Spring Data JPA and Hibernate |
| Database driver | MySQL Connector/J; H2 is also included |
| Security | Spring Security, BCrypt password encoder, JJWT 0.13.0 |
| Validation | Jakarta Bean Validation / Spring Validation |
| API documentation | Springdoc OpenAPI UI 2.8.14 |
| Caching | Spring Cache and Caffeine 3.2.3 |
| Operations | Spring Boot Actuator |
| Observability | Spring Boot OpenTelemetry support, OTLP configuration, Logback OpenTelemetry appender |
| External HTTP calls | Spring HTTP Service Clients and RestClient |
| Local observability stack | Grafana LGTM Docker image in compose.yml |

## Architecture

The project separates HTTP controllers, service contracts and implementations, repositories, persistence entities, and DTOs. Security, caching, web configuration, and cross-cutting concerns live in dedicated packages.

~~~text
Client
  |
  v
/api REST endpoints
  |
  v
Controllers
  |
  v
Service interfaces and implementations
  |
  +----> Caffeine cache (where configured)
  |
  v
Spring Data repositories
  |
  v
JPA entities / Hibernate
  |
  v
MySQL (configured default) or another configured datasource
~~~

Security filters and request authorization run as part of the Spring Security filter chain before protected controller logic is reached. The diagram is a high-level view; individual flows vary by endpoint.

### Package guide

| Package | Responsibility |
|---|---|
| auth | Registration and login endpoints; JWT returned after successful login |
| company | Company API, service contract and implementation |
| job | Employer job operations and employer-side application management |
| user | Candidate profiles, resume/picture uploads, saved jobs, and applications |
| contact | Public contact submissions and administrator workflows |
| security | Security filter chain, route groups, authentication providers, JWT validation, CSRF endpoint and CORS properties |
| entity | JPA entities and their relationships |
| dto | API request/response models for login, company, job, profile and application workflows |
| repository | Spring Data repositories and custom queries |
| cache | Caffeine cache configuration |
| aspects | Registration validation, method timing/logging, exception logging and successful-login audit logging |
| Audit | Current-auditor provider for JPA auditing |
| exception | Central exception-to-HTTP-response handling |
| client | Example REST endpoints and Spring HTTP clients for JSONPlaceholder |
| config.web | API versioning and the common /api path prefix |
| otel | OpenTelemetry Logback appender initialization |
| util | Shared application utility methods and DTO mapping helpers |

### Main domain model

- **Company** has a collection of jobs.
- **Job** belongs to a company and contains role details such as location, work type, employment type, experience level, salary range, description, requirements, benefits, deadline, and status.
- **JobPortalUser** has a role and may be associated with a company. A user can have a profile, save multiple jobs, and submit job applications.
- **Profile** stores candidate information and optional profile-picture and resume data.
- **JobApplication** links a user to a job and tracks application time, status, cover letter, and employer notes.
- **Contact** stores messages submitted through the contact endpoint.
- **Role** represents the role assigned to an account.

Entities inherit common auditing fields from BaseEntity. JPA auditing is enabled at application startup through AuditAwareImpl.

## API conventions

The web configuration adds the /api prefix and configures media-type parameter API versioning. The default API version is 1.0; supported versions are configured as 1.0, 2.0, and 3.0.

For versioned controller mappings, send the version as a parameter on the vendor media type in the Accept header, for example:

~~~http
Accept: application/vnd.jobportal+json;v=1.0
~~~

Some mappings in the code do not declare an explicit version. The endpoint inventory below reflects the mappings currently defined; check Swagger UI for the effective runtime specification.

## API endpoint reference

All paths below include the common /api prefix.

### Authentication

| Method | Endpoint | Purpose |
|---|---|---|
| POST | /auth/register/public | Register a job-seeker account |
| POST | /auth/login/public | Authenticate and return user details with a JWT |

Successful login returns a LoginResponseDto containing a message, user details, and token. Registration assigns the job-seeker role.

### Companies

| Method | Endpoint | Purpose |
|---|---|---|
| GET | /companies/public | List companies with active jobs |
| POST | /companies/admin | Create a company |
| GET | /companies/admin | List companies for administration |
| PUT | /companies/{id}/admin | Update company details |
| DELETE | /companies/{id}/admin | Delete a company |

### Jobs and employer application management

| Method | Endpoint | Purpose |
|---|---|---|
| GET | /jobs/employer | List jobs associated with the authenticated employer's company |
| POST | /jobs/employer | Create a job for the authenticated employer's company |
| PATCH | /jobs/{jobId}/status/employer | Change a job status |
| GET | /jobs/applications/{jobId}/employer | List applications for a job |
| PATCH | /jobs/applications/employer | Update an application's status and notes |

Job statuses accepted by the service are DRAFT, ACTIVE, and CLOSED. New jobs are created with DRAFT status.

### Candidate profile and job-seeker workflows

| Method | Endpoint | Purpose |
|---|---|---|
| PUT | /users/profile/jobseeker | Create or update profile, optionally including picture and resume |
| GET | /users/profile/jobseeker | Retrieve profile details |
| GET | /users/profile/picture/jobseeker | Download profile picture |
| GET | /users/profile/resume/jobseeker | Download resume |
| POST | /users/saved-jobs/{jobId}/jobseeker | Save a job |
| DELETE | /users/saved-jobs/{jobId}/jobseeker | Remove a saved job |
| GET | /users/saved-jobs/jobseeker | List saved jobs |
| POST | /users/job-applications/jobseeker | Apply for a job |
| DELETE | /users/job-applications/{jobId}/jobseeker | Withdraw an application |
| GET | /users/job-applications/jobseeker | List the authenticated candidate's applications |

The profile update endpoint consumes multipart/form-data. Its parts are profile (JSON text), optional profilePicture, and optional resume.

The application service prevents a user from applying to the same job more than once and updates the job's application count when an application is created or withdrawn.

### Administrator user management

| Method | Endpoint | Purpose |
|---|---|---|
| GET | /users/search/admin?email={email} | Find a user by email |
| PATCH | /users/{userId}/role/employer/admin | Promote a job seeker to employer |
| PATCH | /users/{userId}/company/{companyId}/admin | Assign a company to an employer |

### Contact messages

| Method | Endpoint | Purpose |
|---|---|---|
| POST | /contacts/public | Submit a contact message |
| GET | /contacts/admin | List new contact messages |
| GET | /contacts/sort/admin?sortBy=createdAt&sortDir=asc | List new messages with sorting |
| GET | /contacts/page/admin?pageNumber=0&pageSize=10&sortBy=createdAt&sortDir=asc | List new messages with pagination and sorting |
| PATCH | /contacts/{id}/status/admin | Close a contact message |

### Supporting and demonstration endpoints

| Method | Endpoint | Purpose |
|---|---|---|
| GET | /csrf-token/public | Obtain a CSRF token |
| GET | /logging/public | Emit sample log levels |
| GET | /posts, /posts/{id} | Read example post resources |
| POST | /posts | Create an example post resource |
| PUT | /posts/{id} | Update an example post resource |
| DELETE | /posts/{id} | Delete an example post resource |
| GET | /todos, /todos/{id} | Read example todo resources |
| POST | /todos | Create an example todo resource |
| PUT | /todos/{id} | Update an example todo resource |
| DELETE | /todos/{id} | Delete an example todo resource |

The post and todo controllers use Spring HTTP service clients configured against JSONPlaceholder. They are demonstration/integration endpoints rather than part of the recruitment domain.

## Security and configuration notes

The security configuration groups routes as public, authenticated, job-seeker, employer, and administrator paths. It installs a JWT validation filter, disables form login and HTTP Basic, configures CSRF with a cookie token repository, and obtains CORS settings from app.cors.* properties.

The JWT utility uses the JWT_SECRET environment property when generating and validating tokens. **Set a strong secret outside source control** and do not rely on the fallback constant in the code.

**Important implementation detail:** the production authentication provider compares the submitted password against the stored BCrypt hash. The non-production provider currently loads the user and returns an authenticated principal without verifying the submitted password. Do not treat non-production authentication as secure; correct this behavior before using the application with real accounts or exposing it to an untrusted network.

The current configuration also exposes all Actuator endpoints and allows configuration/environment values to be shown. Restrict management endpoints and sensitive values before any public deployment. Review the local database credentials and Docker volume path in compose.yml before sharing or running it on another machine.

## Configuration and database

The default application.properties configures MySQL:

- Host: DATABASE_HOST (default localhost)
- Port: DATABASE_PORT (default 3306)
- Database: DATABASE_NAME (default jobportal)
- Username: DATABASE_USERNAME (default root)
- Password: DATABASE_PASSWORD (has a development fallback in the current file)

The application uses Hibernate schema update mode (spring.jpa.hibernate.ddl-auto=update). H2 and the H2 console dependency are included, but the default datasource configuration is MySQL. The prod and qa property files override some datasource, cache, and CORS settings.

Before running outside your local environment, supply credentials using environment variables, remove hard-coded secrets from configuration, and choose an appropriate schema migration strategy.

## Run locally

### Prerequisites

- JDK 25
- Git
- MySQL 8.x, or a datasource configured for your environment
- Docker Compose is optional and available for the repository's local MySQL and Grafana LGTM services

### 1. Clone the repository

~~~bash
git clone https://github.com/Munyat/JobPortal.git
cd JobPortal
~~~

### 2. Configure the database

Create a MySQL database named jobportal, or configure the connection with environment variables:

~~~bash
export DATABASE_HOST=localhost
export DATABASE_PORT=3306
export DATABASE_NAME=jobportal
export DATABASE_USERNAME=jobportal_user
export DATABASE_PASSWORD='replace-with-a-local-password'
~~~

Create the database user and grant it access to the database using your local MySQL administration tools. Set JWT_SECRET to a sufficiently strong secret that meets the signing algorithm's key-length requirements.

The checked-in compose.yml currently includes a machine-specific host volume path and development credentials. Edit those values for your environment before starting the Compose database service.

### 3. Start the application

On Linux/macOS:

~~~bash
./mvnw spring-boot:run
~~~

On Windows:

~~~powershell
.\mvnw.cmd spring-boot:run
~~~

The default application HTTP port is Spring Boot's 8080. The management server is configured separately on port 9090, with the base path /jobportal/actuator.

### 4. Build and test

~~~bash
./mvnw clean package
./mvnw test
~~~

The repository currently contains a minimal Spring Boot context test. These commands verify the build and run the checked-in tests; they should not be interpreted as comprehensive endpoint or security coverage.

## API documentation and operations

Springdoc OpenAPI UI is configured at /swagger-ui.html, and the OpenAPI document is normally served under /v3/api-docs. Because the application applies a global /api prefix, check these paths in the running application; the security configuration explicitly allows the corresponding /api documentation paths.

The Actuator management server is configured on port 9090 with base path /jobportal/actuator. Do not expose management endpoints publicly without first reviewing access control and information disclosure settings.

The checked-in compose.yml also defines grafana/otel-lgtm with ports 3000, 4317, and 4318 for local telemetry experimentation. The application's OTLP configuration points to the local collector on port 4318.

## Testing

Run the test suite with:

~~~bash
./mvnw test
~~~

For a stronger engineering baseline, add tests covering authentication for each profile, role restrictions, job application lifecycle rules, profile uploads, validation failures, repository queries, and API version negotiation.

## Known follow-up work

- Ensure password verification is performed in every authentication profile.
- Remove fallback credentials and secrets from source code and configuration.
- Restrict Actuator endpoints and hide sensitive environment/configuration values.
- Add integration and security tests for the main API workflows.
- Replace Hibernate schema auto-update with versioned migrations for shared environments.
- Add examples of representative request/response payloads and document validation/error contracts.
- Make the Docker Compose volume path portable and move its credentials to environment variables.

## Author

**Brian Kipkirui Cheruiyot** — [GitHub @Munyat](https://github.com/Munyat)

## License

No explicit open-source license is configured in the repository. Unless a license is added, the project should not be assumed to permit unrestricted redistribution or reuse.
