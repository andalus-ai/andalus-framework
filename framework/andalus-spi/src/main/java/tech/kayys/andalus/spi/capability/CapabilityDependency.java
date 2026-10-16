package tech.kayys.andalus.spi.capability;

import java.util.Objects;

/**
 * Declares a relationship from one capability to another capability.
 *
 * <p>A dependency may be required or optional.</p>
 *
 * <p>This type describes capability topology only. It does not perform
 * authorization or policy enforcement.</p>
 */
public record CapabilityDependency(
        String capabilityId,
        boolean required
) {

    public CapabilityDependency {
        Objects.requireNonNull(capabilityId, "capabilityId cannot be null");

        if (capabilityId.isBlank()) {
            throw new IllegalArgumentException("capabilityId cannot be blank");
        }
    }

    public static CapabilityDependency required(String capabilityId) {
        return new CapabilityDependency(capabilityId, true);
    }

    public static CapabilityDependency optional(String capabilityId) {
        return new CapabilityDependency(capabilityId, false);
    }
}
