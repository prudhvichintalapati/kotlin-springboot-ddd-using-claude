# Kotlin Spring Boot DDD Bootstrap Project

A production-ready bootstrap project for building applications using **Domain-Driven Design (DDD)** and **Hexagonal Architecture** (Ports & Adapters).

## Architecture Overview

This project follows the **Hexagonal Architecture** pattern with clear separation of concerns:

```
src/main/kotlin/com/example/ddd/
├── domain/                    # Core business logic (innermost layer)
│   ├── model/                 # Entities, Value Objects
│   └── port/                  # Repository interfaces (ports)
├── application/              # Use cases / Application services
│   ├── dto/                   # Data Transfer Objects
│   └── service/               # Application services
├── infrastructure/            # Adapters (outermost layer)
│   ├── adapter/
│   │   ├── api/              # REST controllers
│   │   └── persistence/      # JPA repositories, adapters
│   └── config/                # Spring configuration
```

### Layers

| Layer | Responsibility | Dependencies |
|-------|---------------|--------------|
| **Domain** | Business rules, entities, value objects | None (pure Kotlin) |
| **Application** | Use cases, orchestration | Domain |
| **Infrastructure** | External integrations (DB, API) | Domain, Application |

## Quick Start

### Prerequisites

- Java 17+
- Gradle 8.x (wrapper included)

### Clone and Run

```bash
# Clone the repository
git clone https://github.com/prudhvichintalapati/kotlin-springboot-ddd-using-claude.git
cd kotlin-springboot-ddd-using-claude

# Run the application
./gradlew bootRun
```

The application starts on `http://localhost:8080`

- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **API Docs**: http://localhost:8080/api-docs

## Working with Claude Code

This project is designed so you can give Claude a **feature** or **bug fix** requirement, and it will implement it following DDD + Hexagonal Architecture.

### How to Request Features

Simply tell Claude what you want to build. For example:

> "Add a Customer domain with CRUD operations"

Claude will:
1. Create the domain model (entity, value objects) in `domain/model/`
2. Define repository port in `domain/port/`
3. Create application service in `application/service/`
4. Implement the adapter in `infrastructure/adapter/persistence/`
5. Create REST controller in `infrastructure/adapter/api/`

### Example: Adding a New Domain

When you request a new feature like "User Management", Claude will create:

```
domain/
├── model/
│   └── User.kt              # User entity, UserId value object
├── port/
│   └── UserRepository.kt    # Repository interface (port)

application/
├── dto/
│   └── UserDto.kt          # Request/Response DTOs
└── service/
    └── UserService.kt      # Use case orchestration

infrastructure/
├── adapter/
│   ├── api/
│   │   └── UserController.kt
│   └── persistence/
│       ├── UserEntity.kt
│       ├── UserJpaRepository.kt
│       └── UserRepositoryAdapter.kt
```

### Project Structure Conventions

When implementing features, Claude follows these conventions:

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

## Available Commands

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

## Example API Usage

### Create an Order

```bash
curl -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -d '{"customerId": "550e8400-e29b-41d4-a716-446655440000"}'
```

### Add Item to Order

```bash
curl -X POST http://localhost:8080/api/orders/{orderId}/items \
  -H "Content-Type: application/json" \
  -d '{
    "productId": "660e8400-e29b-41d4-a716-446655440001",
    "productName": "Laptop",
    "quantity": 1,
    "price": 999.99
  }'
```

### Confirm Order

```bash
curl -X POST http://localhost:8080/api/orders/{orderId}/confirm
```

### Get Order

```bash
curl http://localhost:8080/api/orders/{orderId}
```

## Environment Variables

| Variable | Default | Description |
|----------|---------|-------------|
| `DATABASE_URL` | `jdbc:h2:mem:ddbdemo` | Database connection URL |
| `DATABASE_USERNAME` | `sa` | Database username |
| `DATABASE_PASSWORD` | (empty) | Database password |
| `DATABASE_DRIVER` | `org.h2.Driver` | JDBC driver class |
| `SERVER_PORT` | `8080` | HTTP server port |

### PostgreSQL Configuration

To use PostgreSQL:

```bash
export DATABASE_URL=jdbc:postgresql://localhost:5432/mydb
export DATABASE_USERNAME=postgres
export DATABASE_PASSWORD=secret
export DATABASE_DRIVER=org.postgresql.Driver
./gradlew bootRun
```

## Claude Code Skillsets

This project includes configurable skill modules that Claude uses when implementing features. See [`.claude/skills/claude-code-skills.md`](.claude/skills/claude-code-skills.md) for detailed skill definitions.

### Enable/Disable Skills

Edit `.claude/skills/enabled.json` to enable or disable specific capabilities:

```json
{
  "skills": {
    "architecture-clean-ddd": true,
    "hexagonal-ports-adapters": true,
    "gherkin-behavior-specs": true,
    "cucumber-step-definitions": true,
    "unit-test-tdd": true,
    "adapter-integration-tester": true,
    "infrastructure-as-code-validator": true,
    "contract-pact-testing": true
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

### Production Grade Extras

When you enable additional skills, Claude will automatically use them:

- **unit-test-tdd**: Creates unit tests alongside domain logic with 90%+ coverage
- **gherkin-behavior-specs**: Adds behavior scenarios in `.feature` files
- **adapter-integration-tester**: Adds TestContainers-based integration tests
- **contract-pact-testing**: Sets up Pact contract tests for API boundaries
- **infrastructure-as-code-validator**: Creates Terraform/Pulumi infrastructure code

## License

MIT License