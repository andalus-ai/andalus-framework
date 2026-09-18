package tech.kayys.wayang.harness.model;

import java.util.Objects;

public record ModelResolution(
        ModelDescriptor model,
        ResolutionReason reason
) {
    public ModelResolution {
        Objects.requireNonNull(model, "model cannot be null");
        if (reason == null) reason = ResolutionReason.capabilityMatch("Resolved suitable model");
    }
}
