package tech.kayys.wayang.harness.execution.state;

import java.util.Objects;
import java.util.UUID;

/**
 * Represents a execution id.
 *
 * <p>Its components capture `value`.</p>
 *
 * @param value the value
 */


public record ExecutionId(String value) {
    public ExecutionId {
        Objects.requireNonNull(value, "value");
        if (value.isBlank()) {
            throw new IllegalArgumentException("Execution id must not be blank");
        }
    }

    public static ExecutionId of(String value) {
        return new ExecutionId(value);
    }

    public static ExecutionId generate() {
        return new ExecutionId("exec-" + UUID.randomUUID());
    }
}
