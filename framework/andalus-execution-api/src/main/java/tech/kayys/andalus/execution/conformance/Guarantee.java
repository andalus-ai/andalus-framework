package tech.kayys.andalus.execution.conformance;

/**
 * Concrete behavioral guarantees promised by Andalus under defined conditions.
 */
public enum Guarantee {
    ATOMIC_COMMIT,
    DURABLE_STATE,
    EFFECT_ISOLATION,
    REPLAYABLE,
    AUDITABLE,
    IDEMPOTENT,
    ORDERED,
    RESUMABLE,
    MIGRATABLE
}
