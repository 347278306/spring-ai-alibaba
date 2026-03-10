# AGENTS.md - Spring AI Alibaba

## Project Overview

Multi-module Maven project (18 modules: Saa-01 to Saa-18) integrating Spring AI with Alibaba's AI services (Qwen).

## Build Commands

### Build & Test
```bash
# Build entire project
mvn clean install

# Build without tests
mvn clean install -DskipTests

# Build single module
cd Saa-01 && mvn clean install

# Compile only
mvn clean compile

# Run all tests
mvn test

# Run single test class
mvn test -Dtest=MyTestClassName

# Run single test method
mvn test -Dtest=MyTestClassName#myTestMethod

# Run tests in specific module
mvn test -pl Saa-01

# Run specific test in module
mvn test -pl Saa-01 -Dtest=MyTestClassName#myTestMethod

# Skip tests
mvn clean install -DskipTests

# Force update dependencies
mvn clean install -U
```

### Code Quality
```bash
# Run Spotless formatter
mvn spotless:apply
mvn spotless:check

# Run Checkstyle
mvn checkstyle:check

# Run all validators
mvn validate
```

### Other Commands
```bash
# Show dependency tree
mvn dependency:tree

# Run Spring Boot app
mvn spring-boot:run -pl Saa-01
```

## Code Style Guidelines

### Naming Conventions
| Element    | Convention       | Example                           |
|------------|------------------|-----------------------------------|
| Classes    | PascalCase       | `ChatService`, `AlibabaChatModel` |
| Interfaces | PascalCase       | `ChatModel`, `StreamingChatModel` |
| Methods    | camelCase        | `chat()`, `generate()`            |
| Variables  | camelCase        | `chatOptions`, `modelName`        |
| Constants  | UPPER_SNAKE_CASE | `DEFAULT_MAX_TOKENS`              |
| Packages   | lowercase        | `com.alibaba.cloud.ai`            |

### Imports Order

1. `com.alibaba.*` / `com.hao.*` (project)
2. `org.springframework.*`
3. `org.*` (other third-party)
4. `java.*` / `javax.*`
5. `static` imports last

- Use explicit imports (no wildcard `*` except static)

### Formatting
- **Indentation**: 4 spaces (no tabs)
- **Line length**: 120 characters max
- **Braces**: Same-line for classes/methods, new line for control statements
- **Annotations**: Separate line above element

### Types

- Use interfaces over concrete types
- Prefer `List`, `Map`, `Set` over implementations
- Use `var` when type is obvious
- Always specify generic types (no raw types)

### Error Handling

- Use specific exception types
- Include meaningful error messages with context
- Use try-with-resources for auto-closeable
- Log exceptions before rethrowing

### Spring Guidelines

- Constructor injection (preferred)
- Use `@Service`, `@Repository`, `@Component` appropriately
- Use `@ConfigurationProperties` for config classes
- Keep controllers thin; delegate to service layer

### Testing

- Test class: `<ClassName>Test` or `<ClassName>IT`
- Test method: `should<ExpectedBehavior>When<Condition>`
- Use JUnit 5 with Spring Boot test annotations
- Use `@MockBean` for mocking
- Keep tests focused and fast

### Documentation

- Javadoc for public APIs (include `@param`, `@return`, `@throws`)
- TODO comments: `// TODO: ...`
- Comment "why" not "what"

### Logging

- Use SLF4J
- Log levels: ERROR > WARN > INFO > DEBUG
- Never log sensitive info (passwords, API keys)

### Async
- Use `@Async` for async methods
- Use `CompletableFuture` for composition

## Project Structure
```
spring-ai-alibaba/
├── pom.xml
├── Saa-01/ ... Saa-18/ (modules)
└── src/main/java/... & src/test/java/...
```

## Dependencies
- Java: 17
- Spring Boot: 3.5.5
- Spring AI: 1.1.2
- Spring AI Alibaba: 1.1.2.0

## Notes

- Multi-module Maven project (packaging: pom)
- Use Spring AI BOM for dependency management
