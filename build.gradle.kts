plugins {
    id("org.springframework.boot") version "3.2.5"
    id("org.jetbrains.kotlin.jvm") version "1.9.22"
    id("org.jetbrains.kotlin.plugin.spring") version "1.9.22"
}

group = "com.example"
version = "0.0.1-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_17
}

repositories {
    mavenCentral()
}

dependencies {
    // Spring Boot Starters
    implementation("org.springframework.boot:spring-boot-starter-web:3.2.5")
    implementation("org.springframework.boot:spring-boot-starter-validation:3.2.5")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa:3.2.5")
    implementation("org.springframework.boot:spring-boot-starter-security:3.2.5")
    implementation("org.springframework.boot:spring-boot-starter-actuator:3.2.5")
    implementation("org.springframework.boot:spring-boot-starter-cache:3.2.5")
    implementation("org.springframework.boot:spring-boot-starter-aop:3.2.5")

    // Kotlin
    implementation("org.jetbrains.kotlin:kotlin-reflect")

    // Database
    runtimeOnly("com.h2database:h2:2.2.224")
    runtimeOnly("org.postgresql:postgresql:42.7.2")

    // Database Migrations
    implementation("org.flywaydb:flyway-core:10.10.0")
    implementation("org.flywaydb:flyway-database-postgresql:10.10.0")

    // JWT
    implementation("io.jsonwebtoken:jjwt-api:0.12.5")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:0.12.5")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.12.5")

    // OpenAPI / Swagger
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.5.0")

    // Logging
    implementation("net.logstash.logback:logstash-logback-encoder:7.4")

    // Caching (Redis)
    implementation("org.springframework.boot:spring-boot-starter-data-redis:3.2.5")

    // Testing
    testImplementation("org.springframework.boot:spring-boot-starter-test:3.2.5")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    // BDD / Cucumber
    testImplementation("io.cucumber:cucumber-spring:7.18.0")
    testImplementation("io.cucumber:cucumber-junit:7.18.0")

    // Integration Testing
    testImplementation("org.testcontainers:testcontainers:1.19.7")
    testImplementation("org.testcontainers:postgresql:1.19.7")
    testImplementation("org.testcontainers:junit-jupiter:1.19.7")
    testImplementation("org.wiremock:wiremock-standalone:3.0.4")

    // Contract Testing
    testImplementation("au.com.dius.pact.consumer:junit5:4.6.6")
    testImplementation("au.com.dius.pact.provider:junit5spring:4.6.6")
}

kotlin {
    jvmToolchain(17)
}

tasks.withType<Test> {
    useJUnitPlatform()
}