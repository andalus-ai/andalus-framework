package tech.kayys.wayang.state.handoff;

import java.util.Objects;
import java.util.UUID;

public record HandoffId(String value) {
    public HandoffId {
        Objects.requireNonNull(value, "value cannot be null");
    }

    public static HandoffId of(String value) {
        return new HandoffId(value);
    }

    public static HandoffId random() {
        return new HandoffId(UUID.randomUUID().toString());
    }
}
