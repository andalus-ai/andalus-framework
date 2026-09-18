package tech.kayys.wayang.harness.controlplane;

import java.util.Objects;
import java.util.UUID;

/**
 * Unique identifier for a control-plane command.
 */
public record CommandId(String value) {

    public CommandId {
        Objects.requireNonNull(value, "CommandId value cannot be null");
        if (value.isBlank()) {
            throw new IllegalArgumentException("CommandId value cannot be blank");
        }
    }

    public static CommandId of(String value) {
        return new CommandId(value);
    }

    public static CommandId generate() {
        return new CommandId("cmd-" + UUID.randomUUID());
    }
}
