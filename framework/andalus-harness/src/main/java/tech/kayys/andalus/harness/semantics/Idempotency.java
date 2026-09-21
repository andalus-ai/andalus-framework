package tech.kayys.andalus.harness.semantics;

/**
 * Idempotency classification for automatic retries and deduplication.
 */
public enum Idempotency {
    PURE,
    IDEMPOTENT,
    KEYED,
    NON_IDEMPOTENT
}
