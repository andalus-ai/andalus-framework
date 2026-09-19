package tech.kayys.wayang.state.provenance;

import java.util.Objects;

public record ProvenanceSubject(String type, String id) {
    public ProvenanceSubject {
        Objects.requireNonNull(type, "type cannot be null");
        Objects.requireNonNull(id, "id cannot be null");
    }

    public static ProvenanceSubject artifact(String artifactId) {
        return new ProvenanceSubject("artifact", artifactId);
    }

    public static ProvenanceSubject state(String stateKey) {
        return new ProvenanceSubject("state", stateKey);
    }
}
