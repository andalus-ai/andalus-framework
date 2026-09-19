package tech.kayys.wayang.state.context;

import java.util.Objects;
import java.util.UUID;

public record ContextSnapshotId(String value) {
    public ContextSnapshotId {
        Objects.requireNonNull(value, "value cannot be null");
        if (value.isBlank()) {
            throw new IllegalArgumentException("value cannot be blank");
        }
    }

    public static ContextSnapshotId of(String value) {
        return new ContextSnapshotId(value);
    }

    public static ContextSnapshotId random() {
        return new ContextSnapshotId(UUID.randomUUID().toString());
    }
}
