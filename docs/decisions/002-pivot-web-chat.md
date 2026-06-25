# ADR-002: Pivot from CLI natural language input to web chat + agentic tool calling

**Status:** Accepted  
**Date:** 2026-06  
**Supersedes:** planned `?` PUT flow on branch `refactor/spring-ai-pgvector`

## Context

The original plan extended the CLI `?` prefix (already working for semantic FIND) to also handle item creation: a classify step would decide FIND vs PUT, and a multi-turn `[y/e/s/n]` loop would confirm the item before persisting it.

Problems identified before implementation:
- Multi-turn confirmation in a REPL loop is complex to implement and awkward to use
- Custom routing logic (classify → FIND or PUT) does not scale — every new AI capability needs new routing code baked into the runner
- CLI is not accessible to family members or future external users; natural language input via CLI is a dead end for a multi-user product

## Decision

- CLI `?` prefix kept for semantic FIND only — no natural language item creation in the CLI
- New branch `feat/web-ai-chat` introduces an AI chat panel in the existing Thymeleaf web UI
- Claude operates as an agent with tool access (`listStorages`, `createItem`, `searchItems`, `updateItem`) — no custom routing, Claude decides what to call
- Stateless chat, confirm-before-create UX

## Consequences

- AI interaction moves to the web UI — accessible from any browser, foundation for multi-user
- Tool calling pattern scales: adding a new capability = new tool definition, no routing changes
- Claude's behavior is non-deterministic — requires good tool definitions and system prompt, harder to unit-test than custom routing
- CLI remains unchanged and fully functional for power/scripting use
