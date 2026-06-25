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

## Branch status — CLOSED

Branch `refactor/spring-ai-pgvector` is closed. Development continues on `feat/web-ai-chat`.

**What was built on this branch:**
- Flyway replaces `ddl-auto=update`; `V1__full_schema.sql` creates schema from scratch
- `attributes` (JSONB) and `embedding` (vector(1536)) columns added to `items`
- Spring AI BOM 1.0.0; Claude (Anthropic) for chat, OpenAI for embeddings
- `EmbeddingService` — computes and stores item embeddings on every create/update
- `ItemSearchService.findNearestItems` — cosine similarity search via pgvector (`<->`)
- `AiAssistant.findItems` — semantic item search from a natural language query
- CLI `?` prefix — triggers semantic search; regular commands unchanged
- 126 tests passing

**Why stopped:** The natural next step (natural language item creation via CLI) hits a ceiling — multi-turn confirmation in a REPL is the wrong UX surface. The new direction moves AI interaction to a web chat panel with agentic tool calling, which scales to multi-user and photo input.

## Architecture

The application has two runtime modes controlled by Spring profiles:

- **`dev` (default)**: Runs as a Spring Boot web application serving a REST API and Thymeleaf UI on port 8080.
- **`cli`**: Activates `ShelveItCommandLineRunner` (a `CommandLineRunner` bean guarded by `@Profile("cli")`), which runs an interactive REPL loop instead of starting the HTTP server.

### Domain model

`Storage` is a self-referential JPA entity forming a strict hierarchy: `RESIDENCE → ROOM → FURNITURE → UNIT`. Each `StorageType` enum value carries a `StorageTypeStrategy` that declares what types it can contain and whether it requires a parent. Hierarchy rules are enforced in `StorageValidatorService` before persistence.

`Item` belongs to one `Storage` and has a list of keyword strings used for search, an `attributes` map (JSONB, for flexible per-item fields: color, brand, size, etc.), and an `embedding` column (vector(1536), for semantic search via pgvector).

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

### `attributes` field — access by layer

`attributes` (`Map<String, Object>`, stored as JSONB) is fully supported at the service and REST layers: clients can pass it in the JSON body of `POST /api/items` and `PUT /api/items/{id}`.

CLI support is intentionally skipped. The field is a free-form key-value map, and expressing that in a CLI flag (e.g. `--attributes color=red,size=M`) adds parsing/validation complexity with little payoff — the AI module will infer and populate attributes from natural language input, making manual CLI entry largely unnecessary.

### AI module

Lives in the `ai` package. Boundary rule: input is plain text, output is a structured result; no direct repository access — service layer interfaces only. Spring AI with Claude (Anthropic) as the chat backend, OpenAI for embeddings (Anthropic has no embedding API).

`AiAssistant` exposes one method: `findItems(String query)` — converts the query to a vector via `EmbeddingService`, then calls `ItemSearchService.findNearestItems`.

#### Embedding text composition

`DefaultItemService` computes an embedding after every create/update via `EmbeddingService`. The text fed to the embedding model follows a strict rule:

- **Attributes present** → `name + attributes` (e.g. `"Red Jacket color:red season:winter"`)
- **No attributes** → `name + keywords` (e.g. `"Red Jacket red winter jacket"`)

Keywords are excluded when attributes exist because in AI mode keywords are derived from attributes — including both would double-weight the same concepts. The rule is strict (no merging) to keep it simple; the marginal accuracy gain from merging overlapping keywords into attribute-present embeddings is not worth the complexity.

#### `EmbeddingService`

Wraps Spring AI's `EmbeddingModel` (OpenAI). Injected as `@Autowired(required = false)` in `DefaultItemService` — if no OpenAI key is configured, embedding computation is silently skipped and items are stored without an embedding (pre-AI mode still works). In tests, `EmbeddingService` is mocked via `@MockitoBean` in `AbstractPostgresIT`.

### Testing

- **Unit tests** (`src/test/.../unit/`) use Mockito and extend no base class.
- **Integration tests** (`src/test/.../integration/`) extend `AbstractPostgresIT`, which starts a PostgreSQL 15 Testcontainer and wires its JDBC URL via `@DynamicPropertySource`.
- Test SQL fixtures live in `src/test/resources/` (`test_data_storages.sql`, `clear_all_data.sql`).