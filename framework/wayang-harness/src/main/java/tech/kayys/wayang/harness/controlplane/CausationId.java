package tech.kayys.wayang.harness.controlplane;

import java.util.Objects;
import java.util.Optional;

/**
 * Causation identifier capturing the immediate antecedent event that provoked this event or command.
 */
public record CausationId(Optional<String> value) {

    public static CausationId of(String value) {
        Objects.requireNonNull(value, "CausationId value cannot be null");
        return new CausationId(Optional.of(value));
    }

    public static CausationId none() {
        return new CausationId(Optional.empty());
    }
}
