# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Commands

```bash
# Build
mvn clean package

# Run all tests
mvn test

# Run a single test class
mvn test -Dtest=StorageServiceCreateTest

# Run a single test method
mvn test -Dtest=StorageServiceCreateTest#shouldCreateStorage

# Run via Docker (web mode, default)
docker-compose up web

# Run via Docker (CLI mode)
docker-compose --profile cli up cli
```

Integration tests require Docker (Testcontainers spins up a PostgreSQL container automatically).

Local development requires a running PostgreSQL instance matching `application-dev.properties`: `localhost:5432`, db `shelveit_dev`, user `dev_user`, password `dev_password`.

## Architecture

The application has two runtime modes controlled by Spring profiles:

- **`dev` (default)**: Runs as a Spring Boot web application serving a REST API and Thymeleaf UI on port 8080.
- **`cli`**: Activates `ShelveItCommandLineRunner` (a `CommandLineRunner` bean guarded by `@Profile("cli")`), which runs an interactive REPL loop instead of starting the HTTP server.

### Domain model

`Storage` is a self-referential JPA entity forming a strict hierarchy: `RESIDENCE → ROOM → FURNITURE → UNIT`. Each `StorageType` enum value carries a `StorageTypeStrategy` that declares what types it can contain and whether it requires a parent. Hierarchy rules are enforced in `StorageValidatorService` before persistence.

`Item` belongs to one `Storage` and has a list of keyword strings used for search.

### CLI pipeline

User input flows through a chain of collaborators assembled in `StringToCommandTranslator`:

1. `CommandParser` → produces a `ParsedCommand` (type + raw args)
2. `CommandValidator` → validates arg count/format
3. `CommandFactory` → instantiates the concrete `BaseCommand` subclass

`ShelveItCommandLineRunner` passes the resulting `BaseCommand` to `DefaultCommandExecutor`, which dispatches via a `switch` over sealed subtypes and calls `ServiceCaller` (a thin façade over the service layer). The result is a `CommandExecutionResult` formatted by `OutputFormatter`.

Any parse/validation failure produces a `BrokenCommand`, which the executor turns into an error message without touching the services.

Help text for each command is loaded from `src/main/resources/help/*.txt` files by `DefaultHelpTextProvider`.

### Web / REST layer

- `StorageRestController` and `ItemRestController` expose a JSON REST API.
- `StorageWebController`, `ItemWebController`, and `HomeController` serve Thymeleaf views from `src/main/resources/templates/`.
- `GlobalExceptionHandler` handles REST errors; `WebControllerAdvice` handles web (HTML) errors.

### Service layer

Each aggregate has an interface + `Default*` implementation:

- `StorageService` / `DefaultStorageService`
- `ItemService` / `DefaultItemService`
- `ItemSearchService` / `DefaultItemSearchService`

DTOs (`*CreateDTO`, `*UpdateDTO`, `*DTO`) are mapped to/from entities via MapStruct mappers (`ItemMapper`, `StorageMapper` + `StorageMapperHelper`).

### Testing

- **Unit tests** (`src/test/.../unit/`) use Mockito and extend no base class.
- **Integration tests** (`src/test/.../integration/`) extend `AbstractPostgresIT`, which starts a PostgreSQL 15 Testcontainer and wires its JDBC URL via `@DynamicPropertySource`.
- Test SQL fixtures live in `src/test/resources/` (`test_data_storages.sql`, `clear_all_data.sql`).