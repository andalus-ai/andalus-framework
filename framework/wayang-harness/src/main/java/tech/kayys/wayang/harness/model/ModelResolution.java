package tech.kayys.wayang.harness.model;

import java.util.Objects;

/**
 * Represents a model resolution.
 *
 * <p>Its components capture `model`, `reason`.</p>
 *
 * @param model the model
 * @param reason the reason
 */


public record ModelResolution(
        ModelDescriptor model,
        ResolutionReason reason
) {
    public ModelResolution {
        Objects.requireNonNull(model, "model cannot be null");
        if (reason == null) reason = ResolutionReason.capabilityMatch("Resolved suitable model");
    }
}
