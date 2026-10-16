package tech.kayys.andalus.skill.runtime;

import tech.kayys.andalus.skill.spi.SkillReference;

import java.util.Map;
import java.util.Objects;

/**
 * Carries the context needed for a single skill resolution.
 *
 * <p>Wrapping the reference in a request object allows additional resolution
 * metadata (tenant, workspace, execution environment, provider preference,
 * locality, compatibility constraints) to be added later without breaking the
 * {@link SkillResolver} contract.</p>
 */
public record SkillResolutionRequest(
        SkillReference reference,
        Map<String, Object> attributes
) {

    public SkillResolutionRequest {
        Objects.requireNonNull(
                reference,
                "reference must not be null"
        );

        attributes = attributes == null
                ? Map.of()
                : Map.copyOf(attributes);
    }

    /**
     * Convenience factory for attribute-free resolution.
     */
    public static SkillResolutionRequest of(
            SkillReference reference) {

        return new SkillResolutionRequest(
                reference,
                Map.of()
        );
    }
}
