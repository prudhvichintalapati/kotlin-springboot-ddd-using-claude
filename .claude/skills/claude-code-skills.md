# Claude Code Skillsets for DDD + Hexagonal Architecture

This file defines the available skill modules. Each skill can be enabled/disabled via configuration.

## Quick Enable/Disable

Edit `.claude/skills/enabled.json` to enable/disable specific skills:

```json
{
  "skills": {
    "architecture-clean-ddd": true,
    "hexagonal-ports-adapters": true,
    "gherkin-behavior-specs": true,
    "cucumber-step-definitions": true,
    "unit-test-tdd": true,
    "adapter-integration-tester": true,
    "infrastructure-as-code-validator": false,
    "contract-pact-testing": false
  }
}
```

---

# Skill Definitions

## 1. architecture-clean-ddd

**Purpose**: Design and implement Domain-Driven Design components with Ubiquitous Language

**When triggered**:
- User mentions "domain", "entity", "value object", "aggregate root", "bounded context"
- User asks to create a new domain model or business concept
- User wants to refactor existing code to follow DDD patterns

**Implementation Guidelines**:

### Ubiquitous Language
- Create a `docs/ubiquitous-language.md` glossary
- Define terms in business language, not technical
- Include examples and aliases

### Entities
- Use `@Entity` annotation (Spring/JPA context)
- Always have a unique identity (`@Id` with UUID or generated)
- Implement `equals()` and `hashCode()` based on identity, not attributes
- Keep business logic in the entity (Domain Model pattern)

```kotlin
@Entity
class Order(
    @Id val id: OrderId,
    val customerId: CustomerId,
    val items: List<OrderItem>,
    val status: OrderStatus,
    val createdAt: Long
) {
    // Business logic - domain model pattern
    fun confirm(): Order = when (status) {
        OrderStatus.PENDING -> copy(status = OrderStatus.CONFIRMED)
        else -> this
    }
}
```

### Value Objects
- Immutable, no identity
- Use `@JvmInline value class` for type-safe wrappers
- Implement via `@Embeddable` for JPA
- Validate in constructor

```kotlin
@JvmInline
value class Money(val amount: Double) {
    init { require(amount >= 0) }
}
```

### Aggregate Roots
- One entity acts as aggregate root
- Other entities reachable only via root
- Repository attaches to aggregate root only
- Invariant enforcement at root level

### Directory Structure
```
src/main/kotlin/com/example/ddd/domain/
├── model/              # Entities, Value Objects
│   ├── {AggregateName}.kt
│   └── {ValueObject}.kt
├── service/           # Domain services (if needed)
├── events/            # Domain events
└── repository/        # Repository interfaces (ports)
```

---

## 2. hexagonal-ports-adapters

**Purpose**: Implement Hexagonal Architecture (Ports & Adapters) for infrastructure isolation

**When triggered**:
- User mentions "port", "adapter", "infrastructure", "driving/driven"
- User wants to isolate business logic from frameworks
- User asks for clean architecture boundaries

**Implementation Guidelines**:

### Port Definitions (Domain Layer - interfaces)

**Inbound Ports** (Driving/Primary) - use cases:
```kotlin
// In application/service/ or domain/port/
interface CreateOrderUseCase {
    fun execute(command: CreateOrderCommand): Order
}
```

**Outbound Ports** (Driven/Secondary) - integrations:
```kotlin
// In domain/port/
interface OrderRepository {
    fun save(order: Order): Order
    fun findById(id: OrderId): Optional<Order>
}

interface NotificationPort {
    fun send(order: Order)
}
```

### Adapter Implementation (Infrastructure Layer)

**Driving Adapters** (Controllers):
```kotlin
// In infrastructure/adapter/api/
@RestController
class OrderController(
    private val createOrderUseCase: CreateOrderUseCase
) {
    @PostMapping("/orders")
    fun create(@RequestBody request: CreateOrderRequest): ResponseEntity<OrderResponse> {
        val order = createOrderUseCase.execute(request.toCommand())
        return ResponseEntity.ok(order.toResponse())
    }
}
```

**Driven Adapters** (Implementations):
```kotlin
// In infrastructure/adapter/persistence/
class OrderRepositoryAdapter(
    private val jpaRepository: OrderJpaRepository
) : OrderRepository {
    override fun save(order: Order): Order = jpaRepository.save(order.toEntity()).toDomain()
}
```

### Dependency Flow
- Domain has NO dependencies on infrastructure
- Application depends on Domain
- Infrastructure depends on Domain and Application
- Use dependency injection to wire adapters to ports

### Configuration
```kotlin
// In infrastructure/config/
@Configuration
class BeanConfiguration {
    @Bean
    fun orderRepository(jpaRepository: OrderJpaRepository): OrderRepository =
        OrderRepositoryAdapter(jpaRepository)
}
```

---

## 3. gherkin-behavior-specs

**Purpose**: Write structured BDD scenarios in Gherkin language

**When triggered**:
- User mentions "behavior", "scenario", "Given/When/Then", "BDD", "acceptance criteria"
- User wants to document feature behavior in structured format

**Implementation Guidelines**:

### File Structure
```
src/test/resources/features/
├── order/
│   ├── order-creation.feature
│   └── order-cancellation.feature
└── customer/
    └── customer-registration.feature
```

### Feature File Format
```gherkin
Feature: Order Management
  As a customer
  I want to manage my orders
  So that I can track my purchases

  Background:
    Given the system is operational

  Scenario: Create a new order
    Given I am a registered customer
    When I create a new order with items
    Then the order should be in "PENDING" status
    And the total should be calculated correctly

  Scenario: Cancel a pending order
    Given I have a pending order
    When I cancel the order
    Then the order status should be "CANCELLED"
    And I should receive a confirmation

  Scenario: Cannot cancel a shipped order
    Given I have a shipped order
    When I attempt to cancel the order
    Then the cancellation should be rejected
    And the order should remain "SHIPPED"
```

### Best Practices
- Use declarative steps (what, not how)
- One scenario per behavior
- Cover happy path and edge cases
- Use scenario outline for data-driven tests

---

## 4. cucumber-step-definitions

**Purpose**: Map Gherkin steps to application service ports

**When triggered**:
- User has Gherkin feature files and needs step definitions
- User wants to run BDD tests
- User asks to "implement tests" for behavior specs

**Implementation Guidelines**:

### Directory Structure
```
src/test/kotlin/com/example/ddd/
├── stepdefinitions/
│   ├── order/
│   │   └── OrderSteps.kt
│   └── hooks/
│       └── CucumberHooks.kt
└── testconfig/
    └── CucumberTestConfig.kt
```

### Step Definition Pattern
```kotlin
@Given("I am a registered customer")
fun iAmARegisteredCustomer() {
    // Use test fixture or setup method
    testCustomer = customerService.register(RegisterCustomerCommand(...))
}

@When("I create a new order with items")
fun iCreateNewOrderWithItems() {
    resultOrder = createOrderUseCase.execute(CreateOrderCommand(
        customerId = testCustomer.id,
        items = listOf(OrderItemCommand(...))
    ))
}

@Then("the order should be in {string} status")
fun orderShouldBeInStatus(status: String) {
    assertEquals(status, resultOrder.status.name)
}

@And("the total should be calculated correctly")
fun totalShouldBeCalculated() {
    val expectedTotal = testOrderItems.sumOf { it.price * it.quantity }
    assertEquals(expectedTotal, resultOrder.totalAmount)
}
```

### Glue Configuration
```kotlin
@CucumberOptions(
    features = ["classpath:features"],
    glue = ["com.example.ddd.stepdefinitions", "com.example.ddd.hooks"]
)
class RunCucumberTest
```

### Test Configuration
```kotlin
@SpringBootTest
@CucumberContextConfiguration
class CucumberIntegrationTestConfig : SpringBootConfiguration
```

---

## 5. unit-test-tdd

**Purpose**: Implement domain logic validation with high test coverage

**When triggered**:
- User mentions "unit test", "TDD", "test coverage", "mock", "assert"
- User wants tests before or alongside implementation
- User asks for test coverage requirements

**Implementation Guidelines**:

### Test Structure
```
src/test/kotlin/com/example/ddd/
├── domain/
│   └── model/
│       └── OrderTest.kt
├── application/
│   └── service/
│       └── OrderServiceTest.kt
└── TestFixtures.kt
```

### Domain Test Pattern (TDD)
```kotlin
class OrderTest {

    @Test
    fun `confirm should change status from PENDING to CONFIRMED`() {
        // Arrange
        val order = Order(
            id = OrderId(UUID.randomUUID()),
            status = OrderStatus.PENDING,
            ...
        )

        // Act
        val confirmed = order.confirm()

        // Assert
        assertEquals(OrderStatus.CONFIRMED, confirmed.status)
    }

    @Test
    fun `confirm should not change already confirmed order`() {
        val order = Order(status = OrderStatus.SHIPPED, ...)
        val result = order.confirm()
        assertEquals(OrderStatus.SHIPPED, result.status)
    }

    @Test
    fun `addItem should increase total by item price times quantity`() {
        val order = Order(totalAmount = Money(0.0), ...)
        val item = OrderItem(productId = ..., price = Money(100.0), quantity = 2)

        val updated = order.addItem(item)

        assertEquals(Money(200.0), updated.totalAmount)
    }
}
```

### Application Service Test (with mocks)
```kotlin
class OrderServiceTest {

    private lateinit var mockRepository: OrderRepository
    private lateinit var orderService: OrderService

    @BeforeEach
    fun setup() {
        mockRepository = mockk()
        orderService = OrderService(mockRepository)
    }

    @Test
    fun `createOrder should save and return new order`() {
        // Arrange
        every { mockRepository.save(any()) } answers { it.invocation.args[0] as Order }

        // Act
        val result = orderService.createOrder(customerId)

        // Assert
        assertEquals(OrderStatus.PENDING, result.status)
        verify { mockRepository.save(any()) }
    }
}
```

### Coverage Requirements
- Domain layer: 90%+ coverage
- Application services: 80%+ coverage
- Critical paths: 100% coverage

---

## 6. adapter-integration-tester

**Purpose**: Test database repositories and external API clients

**When triggered**:
- User mentions "integration test", "repository test", "testcontainer", "external API"
- User wants to test infrastructure layer
- User asks to test database persistence

**Implementation Guidelines**:

### TestContainers Setup
```kotlin
@TestConfiguration
class TestContainersConfig {
    @Bean
    fun postgresContainer(): PostgreSQLContainer<*> =
        PostgreSQLContainer("postgres:15")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test")
}

@SpringBootTest
@Container
class OrderRepositoryIntegrationTest {

    @Container
    static val postgres = PostgreSQLContainer("postgres:15")

    @DynamicPropertySource
    fun configureProperties(dynamicPropertyRegistry: DynamicPropertyRegistry) {
        dynamicPropertyRegistry.add("spring.datasource.url", postgres::getJdbcUrl)
    }
}
```

### Repository Integration Test
```kotlin
@SpringBootTest
class OrderRepositoryAdapterTest {

    @Autowired
    lateinit var jpaRepository: OrderJpaRepository

    private lateinit var adapter: OrderRepositoryAdapter

    @BeforeEach
    fun setup() {
        adapter = OrderRepositoryAdapter(jpaRepository)
        jpaRepository.deleteAll()
    }

    @Test
    fun `save and findById should return the same order`() {
        val order = Order(
            id = OrderId(UUID.randomUUID()),
            customerId = CustomerId(UUID.randomUUID()),
            status = OrderStatus.PENDING,
            items = listOf(OrderItem(productId = ProductId(UUID.randomUUID()), ...)),
            createdAt = System.currentTimeMillis(),
            totalAmount = Money(100.0)
        )

        val saved = adapter.save(order)
        val found = adapter.findById(order.id)

        assertTrue(found.isPresent)
        assertEquals(order.id, found.get().id)
    }
}
```

### External API Client Test (WireMock)
```kotlin
@SpringBootTest
class NotificationAdapterTest {

    @WireMockTest
    lateinit var wireMockServer: WireMockServer

    @Test
    fun `sendNotification should call external API`() {
        // Given
        wireMockServer.stubFor(
            post(urlEqualTo("/notifications"))
                .willReturn(ok())
        )

        val adapter = NotificationAdapter(wireMockServer.baseUrl())

        // When
        adapter.send(order)

        // Then
        wireMockServer.verify(postRequestedFor(urlEqualTo("/notifications")))
    }
}
```

### TestCategories
```
src/test/kotlin/.../integration/
    ├── persistence/     # Repository tests
    ├── external/        # API client tests
    └── contract/        # Contract tests ( Pact )
```

---

## 7. infrastructure-as-code-validator

**Purpose**: Define and test cloud resources with Terraform/Pulumi

**When triggered**:
- User mentions "infrastructure", "terraform", "pulumi", "cloudformation", "aws", "gcp"
- User wants to define cloud resources as code
- User asks for infrastructure testing

**Implementation Guidelines**:

### Directory Structure
```
infra/
├── terraform/
│   ├── main.tf
│   ├── variables.tf
│   ├── outputs.tf
│   └── tests/
│       └── infrastructure_test.py
└── pulumi/
    ├── index.ts
    └── __tests__/
```

### Terraform Example
```hcl
# infra/terraform/main.tf
variable "environment" {
  description = "Environment name"
  type        = string
}

resource "aws_rds_instance" "postgres" {
  identifier           = "ddddb-${var.environment}"
  engine               = "postgres"
  engine_version       = "15.3"
  instance_class       = "db.t3.micro"
  allocated_storage    = 20
  db_name              = "ddd"
  username             = var.db_username
  password             = var.db_password
}

output "jdbc_url" {
  value = "jdbc:postgresql://${aws_rds_instance.postgres.endpoint}/${var.db_name}"
}
```

### Pulumi Example
```typescript
// infra/pulumi/index.ts
import * as aws from "@pulumi/aws";

const db = new aws.rds.Instance("postgres", {
    engine: "postgres",
    engineVersion: "15.3",
    instanceClass: "db.t3.micro",
    allocatedStorage: 20,
    dbName: "ddd",
    username: "admin",
    password: "password",
});

export const jdbcUrl = `jdbc:postgresql://${db.endpoint}/${db.dbName}`;
```

### Terratest Example
```go
// infra/terraform/tests/infrastructure_test.go
package test

import (
    "testing"
    "github.com/gruntwork-io/terratest/modules/terraform"
    "github.com/stretchr/testify/assert"
)

func TestRdsInstance(t *testing.T) {
    terraformOptions := &terraform.Options{
        TerraformDir: "../terraform",
        Vars: map[string]interface{}{
            "environment": "test",
        },
    }

    defer terraform.Destroy(t, terraformOptions)
    terraform.InitAndApply(t, terraformOptions)

    jdbcUrl := terraform.Output(t, terraformOptions, "jdbc_url")
    assert.Contains(t, jdbcUrl, "postgres")
}
```

---

## 8. contract-pact-testing

**Purpose**: Ensure API consistency across service boundaries

**When triggered**:
- User mentions "contract test", "pact", "consumer-driven contract", "API contract"
- User wants to test API compatibility
- User asks for integration testing between services

**Implementation Guidelines**:

### Consumer Side (Client)
```kotlin
// In src/test/kotlin/.../contract/
@ExtendWith(PactConsumerTestExt::class)
class OrderApiContractTest {

    @Pact(consumer = "order-service", provider = "product-service")
    fun createGetProductsPact(builder: V4PactBuilder) {
        builder
            .given("products exist")
            .uponReceiving("a request for products")
                .path("/api/products")
                .method("GET")
            .willRespondWith()
                .status(200)
                .body(
                    JsonValueMatcher {
                        it.hasValue("[{\"id\": \"123\", \"name\": \"Laptop\"}]")
                    }
                )
    }

    @Test
    @PactTestFor(pactMethod = "createGetProductsPact")
    fun `should fetch products from provider`() {
        val result = productClient.getProducts()
        assertEquals(1, result.size)
        assertEquals("Laptop", result.first().name)
    }
}
```

### Provider Side (Server)
```kotlin
@SpringBootTest
@Provider("order-service")
@PactFolder("pacts")
class OrderApiContractVerificationTest {

    @TestTemplate
    @ExtendWith(PactVerificationInvocationContextProvider::class)
    fun verifyPact(pactVerificationContext: PactVerificationContext) {
        pactVerificationContext.verifyInteraction()
    }
}
```

### Pact Broker Configuration
```yaml
# pactflow-config.yaml
pact:
  broker:
    url: https://your-broker.pactflow.io
    token: ${PACT_BROKER_TOKEN}
```

---

## Production Grade Extras

Additional considerations for production systems:

### 1. API Versioning
- URL-based: `/api/v1/orders`
- Header-based: `Accept: application/vnd.api+json;version=1`
- Always maintain backward compatibility

### 2. Health Checks
```kotlin
@Component
class OrderServiceHealthIndicator : HealthIndicator {
    override fun health(): Health {
        return try {
            // Check repository connection
            Health.up().build()
        } catch (e: Exception) {
            Health.down().withDetail("error", e.message).build()
        }
    }
}
```

### 3. Metrics & Observability
- Micrometer for metrics
- OpenTelemetry for tracing
- Log correlation IDs

### 4. Security
- Input validation with Bean Validation
- Output sanitization
- Rate limiting
- API keys/OAuth for external clients

### 5. Error Handling
```kotlin
@RestControllerAdvice
class GlobalExceptionHandler {
    @ExceptionHandler(IllegalArgumentException::class)
    fun handleBadRequest(e: IllegalArgumentException): ResponseEntity<ErrorResponse> =
        ResponseEntity.badRequest().body(ErrorResponse(e.message))
}
```