# AccountShield

AccountShield is a Spring Boot API for user account management, authentication, refresh token rotation, and email verification.

## Tech Stack

- Java 17
- Spring Boot 4.1
- Spring Security
- Spring Data JPA
- PostgreSQL
- JWT
- springdoc-openapi

## Requirements

- Java 17
- Maven 3.9+ or the bundled `mvnw`
- PostgreSQL 18
- Docker and Docker Compose for containerized runs

## Local Run

1. Start PostgreSQL.
2. Set the required environment variables.
3. Run the app:

```bash
./mvnw spring-boot:run
```

The API runs on `http://localhost:8080`.

## Docker Run

Build and start everything:

```bash
docker compose up --build
```

Services:

- `db` on `localhost:5433`
- `app` on `localhost:8080`

## Environment Variables

### Application

| Variable | Required | Default | Description |
| --- | --- | --- | --- |
| `DATABASE_URL` | Yes | - | JDBC URL for PostgreSQL |
| `DATABASE_USERNAME` | Yes | - | Database username |
| `DATABASE_PASSWORD` | Yes | - | Database password |
| `JWT_SECRET` | No | `accountshield-development-secret-key-change-me-please-32` | Secret used to sign JWTs |
| `JWT_EXPIRATION` | No | `3600000` | Access token lifetime in ms |
| `JWT_REFRESH_EXPIRATION` | No | `2592000000` | Refresh token lifetime in ms |
| `CORS_ALLOWED_ORIGINS` | No | `http://localhost:3000` | Allowed frontend origins |
| `CORS_ALLOW_CREDENTIALS` | No | `true` | Whether CORS allows credentials |
| `CORS_ALLOWED_METHODS` | No | `*` | Allowed HTTP methods |
| `CORS_ALLOWED_HEADERS` | No | `*` | Allowed request headers |
| `CORS_MAX_AGE` | No | `3600` | CORS cache age in seconds |
| `ADMIN_NAME` | No | `admin` | Seed admin name |
| `ADMIN_EMAIL` | No | `admin@gmail.com` | Seed admin email |
| `ADMIN_PASSWORD` | No | `admin@12345` | Seed admin password |
| `PORT` | No | `8080` | HTTP port |

### Database

| Variable | Required | Default | Description |
| --- | --- | --- | --- |
| `POSTGRES_DB` | Yes | - | Database name |
| `POSTGRES_USER` | Yes | - | Database user |
| `POSTGRES_PASSWORD` | Yes | - | Database password |
| `POSTGRES_PORT` | No | `5432` | Internal PostgreSQL port |

## Docker Environment Files

- `.env.db.dev` controls the database container
- `.env.app.dev` controls the application container

## API Notes

- Base API path: `/api`
- Swagger UI: `/swagger-ui/index.html`
- OpenAPI docs: `/v3/api-docs`
- Auth header: `Authorization: Bearer <token>`

## Useful Endpoints

- `POST /api/auth/register`
- `POST /api/auth/login`
- `POST /api/auth/refresh`
- `POST /api/auth/verify-email`

