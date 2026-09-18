package tech.kayys.wayang.harness.controlplane;

import tech.kayys.wayang.harness.consistency.state.ExecutionId;

import java.util.Objects;

/**
 * Identifier for an ordered stream of control-plane events.
 */
public record StreamId(String value) {

    public StreamId {
        Objects.requireNonNull(value, "StreamId value cannot be null");
        if (value.isBlank()) {
            throw new IllegalArgumentException("StreamId value cannot be blank");
        }
    }

    public static StreamId of(String value) {
        return new StreamId(value);
    }

    public static StreamId execution(ExecutionId executionId) {
        Objects.requireNonNull(executionId, "ExecutionId cannot be null");
        return new StreamId("execution/" + executionId.value());
    }

    public static StreamId system() {
        return new StreamId("system");
    }
}
