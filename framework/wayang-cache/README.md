# Wayang Cache

The `wayang-cache` module provides small, protocol-neutral caching contracts
for derived content and request deduplication. Its API covers cache keys,
policies, stores, invalidation, and cache loading.

Implement `CacheStore` for the backing store and use `CachePolicy` to define
freshness and reuse behavior. The module is intentionally independent of a
specific cache product.
