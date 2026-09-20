package tech.kayys.wayang.execution.provenance;

import java.util.Objects;

/**
 * Causal context associating an operation with its trigger, parent, and trace.
 */
public record CausalContext(
        String executionId,
        NodeId parent,
        NodeId trigger,
        TraceId trace
) {
    public CausalContext {
        Objects.requireNonNull(executionId, "executionId cannot be null");
        Objects.requireNonNull(parent, "parent cannot be null");
        Objects.requireNonNull(trigger, "trigger cannot be null");
        Objects.requireNonNull(trace, "trace cannot be null");
    }

    public static CausalContext of(String executionId, NodeId parent, NodeId trigger, TraceId trace) {
        return new CausalContext(executionId, parent, trigger, trace);
    }
}
