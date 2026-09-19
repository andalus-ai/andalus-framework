package tech.kayys.wayang.state.provenance;

import java.util.Objects;
import java.util.UUID;

public record ProvenanceId(String value) {
    public ProvenanceId {
        Objects.requireNonNull(value, "value cannot be null");
    }

    public static ProvenanceId of(String value) {
        return new ProvenanceId(value);
    }

    public static ProvenanceId random() {
        return new ProvenanceId(UUID.randomUUID().toString());
    }
}
