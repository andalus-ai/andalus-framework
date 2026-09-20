package tech.kayys.wayang.execution.provenance;

/**
 * Semantic causal and relational edge types in the Temporal Execution Graph.
 */
public enum EdgeType {
    CONTAINS,
    PRECEDES,
    CAUSED_BY,
    TRIGGERED_BY,
    DEPENDS_ON,
    AUTHORIZED_BY,
    DENIED_BY,
    USED_RESOURCE,
    BOUND_TO,
    PRODUCED,
    CONSUMED,
    RETRIED_FROM,
    RECOVERED_FROM,
    CHECKPOINTED_AS,
    MIGRATED_TO,
    DERIVED_FROM
}
