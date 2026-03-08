# AGENTS.md - Spring AI Alibaba Project

## Project Overview

This is a Spring AI Alibaba project using Maven as the build system. It integrates Spring AI with Alibaba's AI services (like Qwen).

## Build Commands

### Build & Test
```bash
# Build the entire project
mvn clean install

# Build without running tests
mvn clean install -DskipTests

# Compile only (no test compilation)
mvn clean compile

# Run tests
mvn test

# Run a single test class
mvn test -Dtest=MyTestClassName

# Run a single test method
mvn test -Dtest=MyTestClassName#myTestMethod

# Skip tests
mvn clean install -DskipTests

# Force update dependencies
mvn clean install -U

# Package as JAR
mvn clean package
```

### Code Quality
```bash
# Run Spotless code formatter (if configured)
mvn spotless:apply

# Check Spotless
mvn spotless:check

# Run Checkstyle (if configured)
mvn checkstyle:check

# Run all validators
mvn validate
```

### Other Useful Commands
```bash
# Show dependency tree
mvn dependency:tree

# Show effective POM
mvn help:effective-pom

# Run Spring Boot application
mvn spring-boot:run

# List available goals
mvn help:describe -Dplugin=...
```

## Code Style Guidelines

### General Principles
- Follow standard Java conventions (Oracle/Java SE style)
- Use Spring Boot best practices
- Keep code clean, readable, and testable

### Naming Conventions

| Element | Convention | Example |
|---------|------------|---------|
| Classes | PascalCase | `ChatService`, `AlibabaChatModel` |
| Interfaces | PascalCase, often with "able" suffix | `ChatModel`, `StreamingChatModel` |
| Methods | camelCase | `chat()`, `generate()` |
| Variables | camelCase | `chatOptions`, `modelName` |
| Constants | UPPER_SNAKE_CASE | `DEFAULT_MAX_TOKENS` |
| Packages | lowercase, dotted | `com.alibaba.cloud.ai` |

### Imports

- **Order** (as per IntelliJ/Java conventions):
  1. `com.alibaba.*` / `com.hao.*` (project imports)
  2. `org.springframework.*` (Spring framework)
  3. `org.*` (other third-party)
  4. `java.*` / `javax.*` (JDK)
  5. `static` imports last
- Use explicit imports (no wildcard `*` except for static imports)
- Group related imports together

### Formatting

- **Indentation**: 4 spaces (no tabs)
- **Line length**: 120 characters max
- **Braces**: Same-line opening brace for classes/methods, new line for control statements
- **Blank lines**: Separate logical code blocks
- **Annotations**: Put on separate line above the element

### Types

- Use interfaces over concrete types for variables/return types when possible
- Prefer `List`, `Map`, `Set` over concrete implementations
- Use `var` for local variables when type is obvious
- Always specify generic types (no raw types)

### Error Handling

- Use specific exception types (not generic `Exception`)
- Include meaningful error messages with context
- Use `try-with-resources` for auto-closeable resources
- Log exceptions before rethrowing
- Use `@ExceptionHandler` in controllers for proper HTTP error responses

### Spring-Specific Guidelines

- Use constructor injection (preferred over field injection)
- Use `@Service`, `@Repository`, `@Component` annotations appropriately
- Follow Spring Boot application properties conventions
- Use `@ConfigurationProperties` for configuration classes
- Keep `@Controller` thin; delegate to `@Service` layer

### Testing

- Test class naming: `<ClassName>Test` or `<ClassName>IT` for integration tests
- Test method naming: `should<ExpectedBehavior>When<Condition>`
- Use `@SpringBootTest`, `@WebMvcTest`, etc. appropriately
- Use `@MockBean` for mocking dependencies
- Keep tests focused and fast

### Documentation

- Use Javadoc for public APIs
- Include `@param`, `@return`, `@throws` in Javadoc
- Use TODO comments for incomplete code: `// TODO: ...`
- Comment "why" not "what" - code explains what, comments explain reasoning

### Logging

- Use SLF4J (`Logger` interface)
- Use appropriate log levels:
  - `ERROR`: Exceptions, failures
  - `WARN`: Potentially problematic situations
  - `INFO`: Application flow
  - `DEBUG`: Detailed debugging info
- Don't log sensitive information (passwords, API keys)

### Async/Concurrency

- Use `@Async` for async methods
- Use `CompletableFuture` for composition
- Be careful with shared mutable state in concurrent code

## Project Structure

```
spring-ai-alibaba/
├── pom.xml
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/hao/...
│   │   └── resources/
│   │       └── application.properties (or .yml)
│   └── test/
│       └── java/
└── .mvn/
```

## Dependencies

Key versions:
- Java: 17
- Spring Boot: 3.5.5
- Spring AI: 1.1.2
- Spring AI Alibaba: 1.1.2.0

## Notes

- This is a multi-module Maven project (packaging: pom)
- Add modules using `<module>` in root pom.xml
- Use Spring AI BOM for dependency management
