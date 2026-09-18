package tech.kayys.wayang.harness.workflow;

import java.util.Objects;
import java.util.UUID;

/**
 * Unique logical identifier for an execution graph.
 */
public record ExecutionGraphId(String value) {

    public ExecutionGraphId {
        Objects.requireNonNull(value, "ExecutionGraphId value cannot be null");
        if (value.isBlank()) {
            throw new IllegalArgumentException("ExecutionGraphId value cannot be blank");
        }
    }

    public static ExecutionGraphId of(String value) {
        return new ExecutionGraphId(value);
    }

    public static ExecutionGraphId generate() {
        return new ExecutionGraphId("graph-" + UUID.randomUUID());
    }
}
