package tech.kayys.wayang.harness.observability.trace;

/**
 * Kind classification of execution spans.
 */
public enum SpanKind {
    AGENT_TURN,
    MODEL,
    TOOL,
    PROCESS,
    RESOURCE,
    WORKSPACE,
    ARTIFACT,
    HUMAN,
    INTERNAL
}
