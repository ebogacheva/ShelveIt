# Development Spec — feat/web-ai-chat

## Goal

Add an AI chat panel to the existing web UI. The user types natural language; Claude decides what to do (find items, create an item, list storages) by calling service-layer tools. Regular CLI commands and the REST API are unchanged.

## Scope

### In scope

- `/api/ai/chat` endpoint — accepts a user message, runs the agentic loop, returns the assistant response and any actions taken
- AI chat panel in the Thymeleaf web UI — message thread, input field, confirm/cancel buttons for create actions
- Tool definitions exposed to Claude: `listStorages`, `createItem`, `searchItems`, `updateItem`
- Confirm-before-create UX: Claude proposes an item (name, attributes, storage), user confirms or cancels
- Stateless sessions — chat history held in the browser for the duration of the page session only

### Out of scope (future)

- User authentication and multi-user support
- Photo / multimodal input
- Persistent chat history (database-backed)
- Loan and reminder tracking

## Key design decisions

**Agentic tool calling over custom routing.** Claude decides what tools to invoke based on the user's message. This replaces the abandoned approach of classifying input as FIND or PUT in the CLI runner — tool calling scales to any future capability without new routing code. See [ADR-002](decisions/002-pivot-web-chat.md).

**Confirm-first for item creation.** Claude proposes the item and waits for explicit user confirmation before calling `createItem`. Reduces unwanted side effects without sacrificing conversational flow.

**Stateless chat.** No `chat_messages` table. Conversation context lives in the HTTP session or request body for the duration of the session. Simplest viable approach; persistence can be added later when multi-user is introduced.

**Simple frontend.** Thymeleaf + minimal JavaScript (fetch API for the chat endpoint). No heavy JS framework. The chat panel is a progressive enhancement on top of the existing UI.

## Planned endpoint

```
POST /api/ai/chat
Body: { "message": "...", "history": [...] }
Response: { "reply": "...", "action": { "type": "CREATE_ITEM", "item": {...} } | null }
```

`history` is the conversation so far, sent by the client each request (stateless pattern).
