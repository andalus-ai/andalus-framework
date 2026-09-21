# Andalus Embedding Module

`andalus-embedding-core` provides a reusable embedding layer for modules like RAG and memory.

## What it includes

- `EmbeddingProvider` interface for pluggable providers.
- `EmbeddingService` for provider/model selection and optional L2 normalization.
- Content-hash embedding cache + request dedup (`tenant + provider + model + normalize + textHash`).
- `EmbeddingBatchPipeline` for async bounded-queue batch embedding with retry/backpressure.
- Multiple built-in providers that work without external API calls:
  - `DeterministicHashEmbeddingProvider`: `hash`, `hash-384`, `hash-768`, `hash-1536`, ...
  - `TfIdfHashEmbeddingProvider`: `tfidf`, `tfidf-256`, `tfidf-512`, ...
  - `CharNgramEmbeddingProvider`: `chargram`, `chargram-256`, `chargram-512`, ...

## Quick usage

```java
EmbeddingProviderRegistry registry = new EmbeddingProviderRegistry(List.of(
        new DeterministicHashEmbeddingProvider(),
        new TfIdfHashEmbeddingProvider(),
        new CharNgramEmbeddingProvider()));

EmbeddingService service = new EmbeddingService(registry, new EmbeddingModuleConfig());
float[] embedding = service.embedOne("hello andalus");

// provider auto-resolves from model name
float[] vector = service.embed(new EmbeddingRequest(
        List.of("risk scoring"),
        "tfidf-512",
        null,
        true)).first();
```

## Notes for other modules

- RAG can call `EmbeddingService.embed(...)` for query/document vectors.
- Memory stores can call `EmbeddingService.embedOne(...)` before persistence/search.
- Provider/model can be overridden per request using `EmbeddingRequest`.
- Tenant strategy is supported via `EmbeddingModuleConfig#setTenantStrategy(...)` and
  `EmbeddingService#embedForTenant(tenantId, request)`.
- Dimension safety checks are enforced: model-declared dimension must match produced vectors.
- `EmbeddingResponse.version()` exposes `embedding_version` for downstream store schema/version control.

## Config from properties/env

- `andalus.embedding.default-provider` or `ANDALUS_EMBEDDING_DEFAULT_PROVIDER`
- `andalus.embedding.default-model` or `ANDALUS_EMBEDDING_DEFAULT_MODEL`
- `andalus.embedding.version` or `ANDALUS_EMBEDDING_VERSION`
- `andalus.embedding.normalize` or `ANDALUS_EMBEDDING_NORMALIZE`
- `andalus.embedding.cache.enabled` or `ANDALUS_EMBEDDING_CACHE_ENABLED`
- `andalus.embedding.cache.max-entries` or `ANDALUS_EMBEDDING_CACHE_MAX_ENTRIES`
- `andalus.embedding.batch.size` or `ANDALUS_EMBEDDING_BATCH_SIZE`
- `andalus.embedding.batch.queue-capacity` or `ANDALUS_EMBEDDING_BATCH_QUEUE_CAPACITY`
- `andalus.embedding.batch.max-retries` or `ANDALUS_EMBEDDING_BATCH_MAX_RETRIES`
- `andalus.embedding.batch.worker-threads` or `ANDALUS_EMBEDDING_BATCH_WORKER_THREADS`
- `andalus.embedding.tenant-strategies` or `ANDALUS_EMBEDDING_TENANT_STRATEGIES`

In Quarkus/MicroProfile runtime, `EmbeddingModuleConfig` is produced from these
`andalus.embedding.*` keys via CDI.

`EmbeddingService` reads runtime config through a reloadable snapshot
(`EmbeddingConfigRuntime`) so tenant strategy updates are picked up without
recreating the service.

Tenant strategy format (`;` separated):
- `tenant-a=tfidf:tfidf-512`
- `tenant-b|hash|hash-384`
