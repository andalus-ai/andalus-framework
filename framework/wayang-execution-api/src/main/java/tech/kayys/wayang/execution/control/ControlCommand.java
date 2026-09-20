package tech.kayys.wayang.execution.control;

import java.time.Instant;
import java.util.Map;
import java.util.Objects;

/**
 * Explicit control command sent to an execution controller.
 */
public record ControlCommand(
        CommandId id,
        String executionId,
        ControlAction action,
        CommandSource source,
        Instant requestedAt,
        Map<String, Object> parameters
) {
    public ControlCommand {
        Objects.requireNonNull(id, "id cannot be null");
        Objects.requireNonNull(executionId, "executionId cannot be null");
        Objects.requireNonNull(action, "action cannot be null");
        if (source == null) {
            source = CommandSource.SYSTEM;
        }
        if (requestedAt == null) {
            requestedAt = Instant.now();
        }
        parameters = parameters == null ? Map.of() : Map.copyOf(parameters);
    }

    public static ControlCommand of(String executionId, ControlAction action, CommandSource source) {
        return new ControlCommand(CommandId.generate(), executionId, action, source, Instant.now(), Map.of());
    }

    public static ControlCommand of(String executionId, ControlAction action, CommandSource source, Map<String, Object> params) {
        return new ControlCommand(CommandId.generate(), executionId, action, source, Instant.now(), params);
    }
}
