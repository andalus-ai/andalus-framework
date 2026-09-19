package tech.kayys.wayang.state.provenance;

import java.util.Map;

public record ProvenanceEnvironment(
        String sandboxId,
        String workerId,
        String runtimeVersion,
        Map<String, Object> attributes
) {
    public ProvenanceEnvironment {
        attributes = attributes == null ? Map.of() : Map.copyOf(attributes);
    }

    public static ProvenanceEnvironment of(String sandboxId, String workerId) {
        return new ProvenanceEnvironment(sandboxId, workerId, "0.0.1", Map.of());
    }
}
