package tech.kayys.wayang.execution.sandbox;

import java.util.Map;
import java.util.Objects;

/**
 * Request to instantiate an execution sandbox with a specific specification.
 */
public record SandboxRequest(
        SandboxId sandboxId,
        SandboxSpec specification,
        Map<String, Object> attributes
) {

    public SandboxRequest {
        sandboxId = sandboxId != null ? sandboxId : SandboxId.generate();
        Objects.requireNonNull(specification, "specification cannot be null");
        attributes = attributes != null ? Map.copyOf(attributes) : Map.of();
    }

    public static SandboxRequest of(SandboxSpec spec) {
        return new SandboxRequest(SandboxId.generate(), spec, Map.of());
    }

    public static SandboxRequest of(SandboxId id, SandboxSpec spec) {
        return new SandboxRequest(id, spec, Map.of());
    }
}
