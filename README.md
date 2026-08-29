# Expense Tracker

A Spring Boot expense-tracking application with form login, JWT-authenticated APIs, MySQL persistence, and administrator reporting.

## Requirements

- Java 17 or later
- Maven 3.6 or later
- MySQL 8.x

## Configuration

The application reads credentials and secrets from environment variables. Do not commit real values to the repository.

| Variable | Required | Description |
| --- | --- | --- |
| `DB_PASSWORD` | Yes | MySQL password |
| `JWT_SECRET` | Yes | Base64-encoded key containing at least 64 random bytes |
| `DB_URL` | No | Defaults to `jdbc:mysql://localhost:3306/expense_management?...` |
| `DB_USERNAME` | No | Defaults to `root` |
| `JWT_EXPIRATION_MS` | No | Defaults to `86400000` (24 hours) |
| `JPA_SHOW_SQL` | No | Defaults to `false` |

Generate a suitable JWT secret with OpenSSL:

```shell
openssl rand -base64 64
```

Example for PowerShell:

```powershell
$env:DB_PASSWORD = "your-local-database-password"
$env:JWT_SECRET = "your-base64-encoded-secret"
```

Create the `expense_management` database and its tables before starting the application. The default Hibernate setting is `validate`, so startup fails when the schema is missing or does not match the entities. For local prototyping only, you can temporarily change `spring.jpa.hibernate.ddl-auto` to `update`.

## Build and run

```shell
mvn test
mvn package
java -jar target/expense-tracker-0.0.1-SNAPSHOT.jar
```

For development, run:

```shell
mvn spring-boot:run
```

The application listens on port `8080` by default.

## API overview

### Authentication

- `POST /api/auth/signup` — create an account
- `POST /api/auth/signin` — obtain a JWT

Sign-in request:

```json
{
  "username": "example",
  "password": "example-password"
}
```

Send the returned token on API requests:

```text
Authorization: Bearer <token>
```

### Expenses

- `GET /api/expenses` — list the current user's expenses
- `POST /api/expenses` — create an expense
- `PUT /api/expenses/{id}` — update an expense owned by the current user
- `DELETE /api/expenses/{id}` — delete an expense owned by the current user
- `GET /api/expenses/report` — export the current user's CSV report

### Administration

Routes under `/admin` and `/api/admin` require the `ROLE_ADMIN` authority.

## Security notes

- Database passwords and JWT signing keys are supplied at runtime and are not stored in the repository.
- Passwords are hashed with BCrypt.
- Browser sessions retain CSRF protection. Bearer-token API requests are authenticated by the JWT filter.

## Testing

Run all tests with:

```shell
mvn test
```
