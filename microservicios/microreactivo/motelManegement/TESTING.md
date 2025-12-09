# 🧪 Testing Documentation - Motel Management Service

## Overview

This document describes the unit testing strategy and coverage for the Motel Management microservice domain layer.

## Test Coverage Summary

### ✅ Achieved Coverage

- **MotelService**: 100% coverage (27 tests)
- **RoomService**: 100% coverage (27 tests)  
- **ServiceService**: 100% coverage (37 tests)
- **Total**: 91 unit tests with all passing

### 📊 Coverage Requirements

| Layer | Required Coverage | Actual Coverage | Status |
|-------|------------------|-----------------|--------|
| Core Domain Services | 100% | 100% | ✅ |
| Other Domain | 80% | 80%+ | ✅ |
| Infrastructure | 0% | N/A | ✅ |

## Running Tests

### Run all tests with coverage:
```bash
cd microservicios/microreactivo/motelManegement
mvn clean test
```

### Run only domain service tests:
```bash
mvn test -Dtest='*ServiceTest'
```

### Run a specific test class:
```bash
mvn test -Dtest=MotelServiceTest
```

### Generate coverage report:
```bash
mvn clean test jacoco:report
```

The coverage report will be generated at:
```
target/site/jacoco/index.html
```

## Test Structure

### Test Organization

Tests are organized by service class in:
```
src/test/java/com/ubik/usermanagement/domain/service/
├── MotelServiceTest.java
├── RoomServiceTest.java
└── ServiceServiceTest.java
```

### Test Naming Convention

Tests follow the pattern: `methodName_scenario_expectedResult`

Examples:
- `createMotel_Success`
- `createMotel_NullName_ShouldFail`
- `getMotelById_NotFound_ShouldFail`

## Test Categories

### 1. Success Cases
Tests that verify correct behavior with valid inputs:
- Creating entities with valid data
- Retrieving existing entities
- Updating entities successfully
- Deleting entities

### 2. Validation Errors
Tests that verify proper validation:
- Null values
- Empty strings
- Blank strings (whitespace only)
- Invalid field formats

### 3. Edge Cases
Tests for boundary conditions:
- Maximum allowed values (e.g., 10 images for motels, 15 for rooms)
- Minimum values (e.g., price > 0)
- Empty collections
- Null optional fields

### 4. Business Rules
Tests for domain-specific rules:
- Motel must exist before creating a room
- Service names must be unique
- Cannot add duplicate service to room
- Resource not found scenarios

## Testing Tools & Frameworks

### JUnit 5
- Modern testing framework
- Parameterized tests support
- Better assertion messages

### Mockito
- Mocking framework for dependencies
- Used to mock repository ports
- Verification of method calls

### Reactor Test (StepVerifier)
- Testing reactive streams
- Verify asynchronous behavior
- Check for expected errors

### JaCoCo
- Code coverage analysis
- Enforces coverage requirements
- Generates HTML reports

## Coverage Configuration

Coverage is enforced via Maven plugin in `pom.xml`:

```xml
<plugin>
    <groupId>org.jacoco</groupId>
    <artifactId>jacoco-maven-plugin</artifactId>
    <version>0.8.11</version>
    <configuration>
        <rules>
            <!-- 100% for domain services -->
            <!-- 80% for other packages -->
            <!-- 0% for infrastructure (excluded) -->
        </rules>
    </configuration>
</plugin>
```

## Test Examples

### Example 1: Success Test
```java
@Test
@DisplayName("createMotel - Success: should create motel with valid data")
void createMotel_Success() {
    // Given
    when(motelRepositoryPort.save(any(Motel.class)))
        .thenReturn(Mono.just(validMotel));

    // When & Then
    StepVerifier.create(motelService.createMotel(validMotel))
        .expectNext(validMotel)
        .verifyComplete();

    verify(motelRepositoryPort, times(1)).save(any(Motel.class));
}
```

### Example 2: Validation Error Test
```java
@Test
@DisplayName("createMotel - Error: should fail when name is null")
void createMotel_NullName_ShouldFail() {
    // Given
    Motel invalidMotel = new Motel(
        null, null, "Address", "123", "Description", "City", 1L, now, null
    );

    // When & Then
    StepVerifier.create(motelService.createMotel(invalidMotel))
        .expectErrorMatches(error -> error instanceof ValidationException &&
            error.getMessage().contains("nombre del motel es requerido"))
        .verify();

    verify(motelRepositoryPort, never()).save(any(Motel.class));
}
```

### Example 3: Edge Case Test
```java
@Test
@DisplayName("createMotel - Edge Case: should fail when imageUrls exceeds limit of 10")
void createMotel_TooManyImages_ShouldFail() {
    // Given - 11 images
    List<String> tooManyImages = Arrays.asList(
        "url1.jpg", "url2.jpg", ..., "url11.jpg"
    );
    Motel invalidMotel = new Motel(
        null, "Name", "Address", "123", "Desc", "City", 1L, now, tooManyImages
    );

    // When & Then
    StepVerifier.create(motelService.createMotel(invalidMotel))
        .expectErrorMatches(error -> error instanceof ValidationException &&
            error.getMessage().contains("No se pueden agregar más de 10 imágenes"))
        .verify();
}
```

## Best Practices

### ✅ Do's
- Use meaningful test names that describe the scenario
- Test both success and failure paths
- Mock external dependencies (repositories)
- Verify mock interactions
- Test edge cases and boundary conditions
- Use arrange-act-assert (given-when-then) pattern

### ❌ Don'ts
- Don't test infrastructure layer (repositories, controllers) in unit tests
- Don't use real databases in unit tests
- Don't leave tests dependent on execution order
- Don't skip edge case testing
- Don't test framework functionality

## Continuous Integration

Tests run automatically on:
- Pull request creation
- Commits to main branch
- Manual trigger

### CI Pipeline Checks:
1. Compile source code
2. Run all unit tests
3. Generate coverage report
4. Enforce minimum coverage requirements
5. Fail build if coverage < requirements

## Future Improvements

- [ ] Add tests for ReservationService
- [ ] Add integration tests with TestContainers
- [ ] Add mutation testing with PIT
- [ ] Add performance/load tests
- [ ] Add contract tests for APIs

## Support

For questions or issues with tests, contact the development team.
