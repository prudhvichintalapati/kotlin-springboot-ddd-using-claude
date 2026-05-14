# Kotlin Spring Boot DDD Bootstrap Project

A production-ready bootstrap project for building applications using **Domain-Driven Design (DDD)** and **Hexagonal Architecture** (Ports & Adapters).

---

## Table of Contents

1. [User Guide](#user-guide) - Quick start, running the app
2. [How to Implement Features](#how-to-implement-features) - Working with Claude Code
3. [Architecture Reference](#architecture-reference) - Layer structure, conventions
4. [Development Guide](#development-guide) - Commands, testing, environment
5. [Production Deployment](#production-deployment) - Docker, CI/CD, configs
6. [Skills Configuration](#skills-configuration) - Enable/disable capabilities

---

## User Guide

### Prerequisites

- Java 17+
- Gradle 8.x (wrapper included)

### Quick Start

```bash
# Clone the repository
git clone git@github.com:prudhvichintalapati/kotlin-springboot-ddd-using-claude.git
cd kotlin-springboot-ddd-using-claude

# Run the application
./gradlew bootRun
```

### Access Points

| URL | Description |
|-----|-------------|
| `http://localhost:8080` | Application |
| `http://localhost:8080/swagger-ui.html` | API Documentation |
| `http://localhost:8080/api-docs` | OpenAPI JSON |
| `http://localhost:8080/actuator/health` | Health Check |

### Run with Docker

```bash
# Using docker-compose (PostgreSQL + Redis + App)
docker-compose up --build

# Or build and run manually
docker build -t ddd-app .
docker run -p 8080:8080 ddd-app
```

---

## How to Implement Features

This project is designed so you can give **Claude Code** a feature or bug fix requirement, and it will implement it following DDD + Hexagonal Architecture.

### Step 1: Tell Claude What You Want

Simply describe what you need:

> "Add a Customer domain with CRUD operations"

> "Add user authentication with login/logout"

> "Add order cancellation with business rules"

### Step 2: Claude Creates the Structure

Claude will automatically create:

```
domain/
├── model/
│   └── Customer.kt              # Entity, value objects
├── port/
│   └── CustomerRepository.kt    # Repository interface (port)

application/
├── dto/
│   └── CustomerDto.kt          # Request/Response DTOs
└── service/
    └── CustomerService.kt       # Use case orchestration

infrastructure/
├── adapter/
│   ├── api/
│   │   └── CustomerController.kt
│   └── persistence/
│       ├── CustomerEntity.kt
│       ├── CustomerJpaRepository.kt
│       └── CustomerRepositoryAdapter.kt
```

### Step 3: Test Your Feature

```bash
# Create customer
curl -X POST http://localhost:8080/api/customers \
  -H "Content-Type: application/json" \
  -d '{"name": "John", "email": "john@example.com"}'

# Get customer
curl http://localhost:8080/api/customers/{id}
```

### Example Feature Requests

| Request | What Claude Builds |
|---------|-------------------|
| "Add Product domain" | Product entity, repository, service, REST endpoints |
| "Add Order workflow" | Order with status transitions, validation rules |
| "Add Inventory management" | Stock tracking, reservation logic, notifications |
| "Add Payment integration" | Payment port, external adapter, transaction handling |

---

## Architecture Reference

### Project Structure

```
src/main/kotlin/com/example/ddd/
├── domain/                    # Core business logic (innermost layer)
│   ├── model/                 # Entities, Value Objects
│   └── port/                  # Repository interfaces (ports)
├── application/              # Use cases / Application services
│   ├── dto/                   # Data Transfer Objects
│   └── service/              # Application services
└── infrastructure/            # Adapters (outermost layer)
    ├── adapter/
    │   ├── api/              # REST controllers
    │   └── persistence/      # JPA repositories, adapters
    ├── config/               # Spring configuration
    └── security/             # JWT authentication
```

### Layer Responsibilities

| Layer | Responsibility | Dependencies |
|-------|---------------|--------------|
| **Domain** | Business rules, entities, value objects, domain events | None (pure Kotlin) |
| **Application** | Use cases, orchestration, DTOs | Domain |
| **Infrastructure** | External integrations (DB, API, Security) | Domain, Application |

### Conventions

1. **Domain Layer** (`domain/`)
   - Entities use `data class` with ID as `@JvmInline value class`
   - Business logic in entity methods
   - Value objects are immutable (`@JvmInline value class`)
   - Repository interfaces (ports) define persistence contracts

2. **Application Layer** (`application/`)
   - Services orchestrate domain operations
   - DTOs for API input/output
   - No direct framework dependencies

3. **Infrastructure Layer** (`infrastructure/`)
   - Adapters implement domain ports
   - Controllers handle HTTP concerns
   - JPA entities for database mapping
   - Security configuration for JWT

---

## Development Guide

### Available Commands

```bash
# Build the project
./gradlew build

# Run tests
./gradlew test

# Run with hot reload
./gradlew bootRun

# Generate IDE project files
./gradlew idea
./gradlew eclipse
```

### Example API Usage

```bash
# Create an Order
curl -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -d '{"customerId": "550e8400-e29b-41d4-a716-446655440000"}'

# Add Item to Order
curl -X POST http://localhost:8080/api/orders/{orderId}/items \
  -H "Content-Type: application/json" \
  -d '{"productId": "660e8400-e29b-41d4-a716-446655440001", "productName": "Laptop", "quantity": 1, "price": 999.99}'

# Confirm Order
curl -X POST http://localhost:8080/api/orders/{orderId}/confirm

# Get Order
curl http://localhost:8080/api/orders/{orderId}
```

### Environment Variables

| Variable | Default | Description |
|----------|---------|-------------|
| `DATABASE_URL` | `jdbc:h2:mem:ddbdemo` | Database connection URL |
| `DATABASE_USERNAME` | `sa` | Database username |
| `DATABASE_PASSWORD` | (empty) | Database password |
| `DATABASE_DRIVER` | `org.h2.Driver` | JDBC driver class |
| `SERVER_PORT` | `8080` | HTTP server port |
| `JWT_SECRET` | (dev default) | JWT signing secret |
| `FLYWAY_ENABLED` | `false` | Enable Flyway migrations |
| `JPA_DDL_AUTO` | `create-drop` | Hibernate schema mode |

### PostgreSQL Configuration

```bash
export DATABASE_URL=jdbc:postgresql://localhost:5432/mydb
export DATABASE_USERNAME=postgres
export DATABASE_PASSWORD=secret
export DATABASE_DRIVER=org.postgresql.Driver
export FLYWAY_ENABLED=true
export JPA_DDL_AUTO=validate
./gradlew bootRun
```

---

## Production Deployment

### Docker Production

```bash
# Build production image
docker build -t ddd-app:latest .

# Run with environment
docker run -d \
  -e SPRING_PROFILES_ACTIVE=prod \
  -e DATABASE_URL=jdbc:postgresql://host:5432/db \
  -e JWT_SECRET=your-production-secret \
  -p 8080:8080 \
  ddd-app:latest
```

### docker-compose (Full Stack)

```bash
# Start PostgreSQL, Redis, and App
docker-compose up -d
```

### CI/CD

The project includes GitHub Actions workflow at `.github/workflows/ci.yaml`:

- Builds on every push to `main`/`develop`
- Runs unit tests
- Builds Docker image
- Pushes to GitHub Container Registry

---

## Skills Configuration

This project includes configurable skill modules for Claude Code. See [`.claude/skills/claude-code-skills.md`](.claude/skills/claude-code-skills.md) for detailed definitions.

### Enable/Disable Skills

Edit `.claude/skills/enabled.json`:

```json
{
  "skills": {
    "architecture-clean-ddd": true,
    "hexagonal-ports-adapters": true,
    "gherkin-behavior-specs": false,
    "cucumber-step-definitions": false,
    "unit-test-tdd": false,
    "adapter-integration-tester": false,
    "infrastructure-as-code-validator": false,
    "contract-pact-testing": false
  }
}
```

### Available Skills

| Skill | Description | Default |
|-------|-------------|---------|
| `architecture-clean-ddd` | Entities, Value Objects, Aggregate Roots | ✅ Enabled |
| `hexagonal-ports-adapters` | Inbound/Outbound Ports, infrastructure isolation | ✅ Enabled |
| `gherkin-behavior-specs` | Given/When/Then scenarios | ❌ Disabled |
| `cucumber-step-definitions` | Map Gherkin to application services | ❌ Disabled |
| `unit-test-tdd` | Domain logic validation with mocks | ❌ Disabled |
| `adapter-integration-tester` | DB repositories, external API tests (TestContainers) | ❌ Disabled |
| `infrastructure-as-code-validator` | Terraform/Pulumi cloud resources | ❌ Disabled |
| `contract-pact-testing` | Consumer-driven contract testing | ❌ Disabled |

---

## License

MIT License