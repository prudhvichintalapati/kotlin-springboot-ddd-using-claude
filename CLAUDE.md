# CLAUDE.md - Project Context for Claude Code

This project implements Domain-Driven Design with Hexagonal Architecture. See `.claude/skills/` for available skill modules.

## Quick Start

```bash
./gradlew bootRun
```

- Swagger: http://localhost:8080/swagger-ui.html
- API Docs: http://localhost:8080/api-docs

## Enabled Skills

Check `.claude/skills/enabled.json` to see which skills are currently enabled:

| Skill | Status |
|-------|--------|
| architecture-clean-ddd | ✅ Enabled |
| hexagonal-ports-adapters | ✅ Enabled |
| gherkin-behavior-specs | ❌ Disabled |
| cucumber-step-definitions | ❌ Disabled |
| unit-test-tdd | ❌ Disabled |
| adapter-integration-tester | ❌ Disabled |
| infrastructure-as-code-validator | ❌ Disabled |
| contract-pact-testing | ❌ Disabled |

## Architecture

```
src/main/kotlin/com/example/ddd/
├── domain/              # Entities, Value Objects, Ports
├── application/         # Use Cases, DTOs
└── infrastructure/     # Adapters (API, Persistence)
```

## How to Work

Tell Claude what feature or fix you need. Example:

> "Add a Customer domain with CRUD operations"

Claude will use the enabled skills to:
1. Create domain models (architecture-clean-ddd)
2. Define ports (hexagonal-ports-adapters)
3. Add tests (if unit-test-tdd enabled)
4. Create infrastructure adapters

## Enable More Skills

Edit `.claude/skills/enabled.json` to enable additional capabilities:

```json
{
  "skills": {
    "unit-test-tdd": true,
    "gherkin-behavior-specs": true,
    "adapter-integration-tester": true
  }
}
```