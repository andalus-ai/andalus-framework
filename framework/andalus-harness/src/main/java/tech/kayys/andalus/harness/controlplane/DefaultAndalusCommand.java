package tech.kayys.andalus.harness.controlplane;

import tech.kayys.andalus.harness.consistency.state.ExecutionId;

import java.time.Instant;
import java.util.Map;
import java.util.Objects;

/**
 * Immutable reference record implementing {@link AndalusCommand}.
 */
public record DefaultAndalusCommand(
        CommandId id,
        CommandType type,
        Instant timestamp,
        ExecutionId executionId,
        Map<String, Object> parameters
) implements AndalusCommand {

    public DefaultAndalusCommand {
        Objects.requireNonNull(id, "CommandId cannot be null");
        Objects.requireNonNull(type, "CommandType cannot be null");
        Objects.requireNonNull(executionId, "ExecutionId cannot be null");
        timestamp = timestamp != null ? timestamp : Instant.now();
        parameters = parameters != null ? Map.copyOf(parameters) : Map.of();
    }

    public static DefaultAndalusCommand of(CommandType type, ExecutionId executionId) {
        return new DefaultAndalusCommand(CommandId.generate(), type, Instant.now(), executionId, Map.of());
    }

    public static DefaultAndalusCommand of(CommandType type, ExecutionId executionId, Map<String, Object> parameters) {
        return new DefaultAndalusCommand(CommandId.generate(), type, Instant.now(), executionId, parameters);
    }
}
