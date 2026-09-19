package tech.kayys.wayang.state.checkpoint;

import java.util.Objects;
import java.util.UUID;

public record CheckpointId(String value) {
    public CheckpointId {
        Objects.requireNonNull(value, "value cannot be null");
    }

    public static CheckpointId of(String value) {
        return new CheckpointId(value);
    }

    public static CheckpointId random() {
        return new CheckpointId(UUID.randomUUID().toString());
    }
}
