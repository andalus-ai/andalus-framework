package tech.kayys.wayang.harness.controlplane;

import java.time.Instant;
import java.util.Objects;

/**
 * Result returned upon evaluating or dispatching a command.
 */
public record CommandResult(
        CommandId commandId,
        boolean accepted,
        String message,
        Instant completedAt
) {

    public CommandResult {
        Objects.requireNonNull(commandId, "CommandId cannot be null");
        message = message != null ? message : "";
        completedAt = completedAt != null ? completedAt : Instant.now();
    }

    public static CommandResult accepted(CommandId id) {
        return new CommandResult(id, true, "Command accepted", Instant.now());
    }

    public static CommandResult rejected(CommandId id, String reason) {
        return new CommandResult(id, false, reason, Instant.now());
    }
}
