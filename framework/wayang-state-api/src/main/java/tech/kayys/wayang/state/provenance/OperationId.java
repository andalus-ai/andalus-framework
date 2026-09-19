package tech.kayys.wayang.state.provenance;

import java.util.Objects;
import java.util.UUID;

public record OperationId(String value) {
    public OperationId {
        Objects.requireNonNull(value, "value cannot be null");
    }

    public static OperationId of(String value) {
        return new OperationId(value);
    }

    public static OperationId random() {
        return new OperationId(UUID.randomUUID().toString());
    }
}
