package tech.kayys.wayang.spi.sandbox;

import java.util.Objects;

public record ResourceLimitCapability(
        ResourceLimitFeature feature,
        ResourceEnforcement enforcement
) {
    public ResourceLimitCapability {
        Objects.requireNonNull(feature, "feature");
        Objects.requireNonNull(enforcement, "enforcement");
    }
}
