package tech.kayys.wayang.spi.sandbox;

import java.util.Objects;

public record EffectiveResourceLimits(
        SandboxLimits requested,
        ResourceLimitStatus enforced
) {
    public EffectiveResourceLimits {
        Objects.requireNonNull(requested, "requested");
        Objects.requireNonNull(enforced, "enforced");
    }
}
