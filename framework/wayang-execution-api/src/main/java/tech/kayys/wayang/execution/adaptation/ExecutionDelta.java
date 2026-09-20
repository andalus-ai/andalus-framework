package tech.kayys.wayang.execution.adaptation;

import java.util.Objects;

/**
 * Atomic mutation describing what changes in the execution environment.
 */
public record ExecutionDelta(
    String field,
    Object oldValue,
    Object newValue,
    String justification
) {
    public ExecutionDelta {
        Objects.requireNonNull(field, "field cannot be null");
        Objects.requireNonNull(justification, "justification cannot be null");
    }

    public static ExecutionDelta of(String field, Object oldValue, Object newValue, String justification) {
        return new ExecutionDelta(field, oldValue, newValue, justification);
    }
}
