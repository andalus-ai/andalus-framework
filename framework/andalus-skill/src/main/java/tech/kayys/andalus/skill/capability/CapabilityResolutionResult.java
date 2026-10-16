package tech.kayys.andalus.skill.capability;

import java.util.Map;
import java.util.Set;

/**
 * Result of evaluating whether a Skill's capability requirements can be
 * satisfied by the current runtime environment.
 *
 * <p>Produced by {@link SkillCapabilityResolver}. The {@link #satisfied()}
 * flag determines whether the Skill may proceed to execution.</p>
 *
 * <p>Important: capability availability is <em>not</em> authorization. A
 * satisfied result means the capability is present; it does not mean the
 * Skill is permitted to use it in all contexts.</p>
 */
public record CapabilityResolutionResult(
        boolean satisfied,
        Set<CapabilityRequirement> required,
        Set<CapabilityRequirement> missing,
        Map<String, Object> attributes
) {

    public CapabilityResolutionResult {
        required   = required   == null ? Set.of() : Set.copyOf(required);
        missing    = missing    == null ? Set.of() : Set.copyOf(missing);
        attributes = attributes == null ? Map.of() : Map.copyOf(attributes);
    }

    /** All declared requirements are satisfiable. */
    public static CapabilityResolutionResult satisfied(
            Set<CapabilityRequirement> required) {
        return new CapabilityResolutionResult(true, required, Set.of(), Map.of());
    }

    /** One or more required capabilities are unavailable. */
    public static CapabilityResolutionResult unsatisfied(
            Set<CapabilityRequirement> required,
            Set<CapabilityRequirement> missing) {
        return new CapabilityResolutionResult(false, required, missing, Map.of());
    }

    /** Trivially satisfied — no capabilities declared. */
    public static CapabilityResolutionResult empty() {
        return new CapabilityResolutionResult(true, Set.of(), Set.of(), Map.of());
    }
}
