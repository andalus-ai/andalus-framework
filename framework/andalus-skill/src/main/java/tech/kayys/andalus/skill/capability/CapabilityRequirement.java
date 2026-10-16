package tech.kayys.andalus.skill.capability;

import java.util.Objects;

/**
 * Represents a single capability requirement declared by a Skill manifest.
 *
 * <p>A capability requirement is just a named capability token plus an
 * indication of whether it is mandatory. The capability system determines
 * which provider satisfies it — the Skill itself never resolves providers.</p>
 *
 * <p>Semantics:</p>
 * <ul>
 *   <li>{@code required = true}  — Skill cannot execute without this capability.</li>
 *   <li>{@code required = false} — Skill degrades gracefully if unavailable.</li>
 * </ul>
 */
public record CapabilityRequirement(String capabilityId, boolean required) {

    public CapabilityRequirement {
        Objects.requireNonNull(capabilityId, "capabilityId");
        if (capabilityId.isBlank()) {
            throw new IllegalArgumentException("capabilityId must not be blank");
        }
    }

    /** Create a mandatory capability requirement. */
    public static CapabilityRequirement required(String capabilityId) {
        return new CapabilityRequirement(capabilityId, true);
    }

    /** Create an optional capability requirement. */
    public static CapabilityRequirement optional(String capabilityId) {
        return new CapabilityRequirement(capabilityId, false);
    }
}
