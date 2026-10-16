package tech.kayys.andalus.skill.runtime;

import java.util.List;

/**
 * Declarative configuration for skill resolution behaviour.
 *
 * <p>This record lets deployers control the resolution chain through
 * configuration rather than code.  For example, a community installation can
 * omit the {@code "remote"} provider and set {@code allowRemote = false} so
 * skills are always resolved locally.</p>
 *
 * <pre>{@code
 * skill:
 *   resolution:
 *     providers:
 *       - builtin
 *       - plugin
 *     allow-remote: false
 * }</pre>
 */
public record SkillResolutionPolicy(
        List<String> providerOrder,
        boolean allowRemote,
        boolean allowDisabled
) {

    public SkillResolutionPolicy {
        providerOrder = providerOrder == null
                ? List.of()
                : List.copyOf(providerOrder);
    }

    /** Default policy: builtin → plugin, no remote, no disabled. */
    public static SkillResolutionPolicy defaults() {
        return new SkillResolutionPolicy(
                List.of("builtin", "plugin"),
                false,
                false
        );
    }
}
