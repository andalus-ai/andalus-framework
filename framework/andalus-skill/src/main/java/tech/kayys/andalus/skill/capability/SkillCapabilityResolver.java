package tech.kayys.andalus.skill.capability;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

/**
 * SPI for determining whether a Skill's declared capability requirements can
 * be satisfied by the current runtime environment.
 */
public interface SkillCapabilityResolver {

    CompletableFuture<CapabilityResolutionResult> resolve(
            Set<CapabilityRequirement> requirements);

    static SkillCapabilityResolver alwaysSatisfied() {
        return requirements -> CompletableFuture.completedFuture(
                requirements == null || requirements.isEmpty()
                        ? CapabilityResolutionResult.empty()
                        : CapabilityResolutionResult.satisfied(requirements));
    }

    static SkillCapabilityResolver alwaysUnsatisfied() {
        return requirements -> {
            Set<CapabilityRequirement> reqs =
                    requirements == null ? Set.of() : requirements;
            return CompletableFuture.completedFuture(
                    CapabilityResolutionResult.unsatisfied(reqs, reqs));
        };
    }
}
