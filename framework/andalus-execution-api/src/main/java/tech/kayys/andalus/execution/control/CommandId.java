package tech.kayys.andalus.execution.control;

import java.util.Objects;
import java.util.UUID;

/**
 * Unique identifier for a control command.
 */
public record CommandId(String value) {
    public CommandId {
        Objects.requireNonNull(value, "value cannot be null");
        if (value.isBlank()) {
            throw new IllegalArgumentException("value cannot be blank");
        }
    }

    public static CommandId of(String value) {
        return new CommandId(value);
    }

    public static CommandId generate() {
        return new CommandId("cmd-" + UUID.randomUUID());
    }
}
