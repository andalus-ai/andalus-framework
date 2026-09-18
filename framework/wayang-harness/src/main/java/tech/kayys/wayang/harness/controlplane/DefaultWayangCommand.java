package tech.kayys.wayang.harness.controlplane;

import tech.kayys.wayang.harness.consistency.state.ExecutionId;

import java.time.Instant;
import java.util.Map;
import java.util.Objects;

/**
 * Immutable reference record implementing {@link WayangCommand}.
 */
public record DefaultWayangCommand(
        CommandId id,
        CommandType type,
        Instant timestamp,
        ExecutionId executionId,
        Map<String, Object> parameters
) implements WayangCommand {

    public DefaultWayangCommand {
        Objects.requireNonNull(id, "CommandId cannot be null");
        Objects.requireNonNull(type, "CommandType cannot be null");
        Objects.requireNonNull(executionId, "ExecutionId cannot be null");
        timestamp = timestamp != null ? timestamp : Instant.now();
        parameters = parameters != null ? Map.copyOf(parameters) : Map.of();
    }

    public static DefaultWayangCommand of(CommandType type, ExecutionId executionId) {
        return new DefaultWayangCommand(CommandId.generate(), type, Instant.now(), executionId, Map.of());
    }

    public static DefaultWayangCommand of(CommandType type, ExecutionId executionId, Map<String, Object> parameters) {
        return new DefaultWayangCommand(CommandId.generate(), type, Instant.now(), executionId, parameters);
    }
}
