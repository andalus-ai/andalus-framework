package tech.kayys.wayang.spi.sandbox;

import java.util.Objects;

public record NetworkDestination(
        NetworkDestinationType type,
        String value
) {
    public NetworkDestination {
        type = Objects.requireNonNull(type, "type");
        value = Objects.requireNonNull(value, "value").trim();
        if (value.isBlank()) {
            throw new IllegalArgumentException("value cannot be blank");
        }
    }
}
