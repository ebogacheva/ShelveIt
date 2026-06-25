# ADR-001: PostgreSQL + pgvector + Spring AI

**Status:** Accepted  
**Date:** 2026-06

## Context

ShelveIt needed a storage backend that could support:
- A structured hierarchy (RESIDENCE → ROOM → FURNITURE → UNIT → Item)
- Flexible per-item metadata (color, brand, size — varies by item type)
- Semantic search ("find my winter jacket" without exact keyword match)
- Future: item history, loans, reminders

MongoDB was considered for flexible schema. A separate vector database (Pinecone, Weaviate) was considered for embeddings.

## Decision

Single PostgreSQL database with:
- `JSONB` column on `items` for flexible attributes
- `pgvector` extension for embedding storage and cosine similarity search
- Spring AI (BOM 1.0.0) with Claude (Anthropic) as the chat/generation backend and OpenAI for embeddings (Anthropic has no embedding API)
- Flyway for schema migrations

## Consequences

- One database for all data — ACID guarantees, no sync between stores
- pgvector cosine search (`<->`) is fast enough for personal/small-team scale
- OpenAI dependency for embeddings; if the key is absent, embedding is silently skipped and items are stored without a vector (pre-AI mode still works)
- Spring AI abstracts provider differences; switching embedding provider later requires minimal code change
