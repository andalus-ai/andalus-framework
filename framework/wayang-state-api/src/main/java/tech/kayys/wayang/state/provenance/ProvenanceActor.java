package tech.kayys.wayang.state.provenance;

import java.util.Map;
import java.util.Objects;

public record ProvenanceActor(ProvenanceActorType type, String id, Map<String, Object> attributes) {
    public ProvenanceActor {
        Objects.requireNonNull(type, "type cannot be null");
        Objects.requireNonNull(id, "id cannot be null");
        attributes = attributes == null ? Map.of() : Map.copyOf(attributes);
    }

    public static ProvenanceActor of(ProvenanceActorType type, String id) {
        return new ProvenanceActor(type, id, Map.of());
    }
}
