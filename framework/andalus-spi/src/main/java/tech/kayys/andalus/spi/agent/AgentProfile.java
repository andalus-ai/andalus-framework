package tech.kayys.andalus.spi.agent;

import java.time.Duration;
import java.util.Map;
import java.util.Set;

/**
 * Runtime specialization of an AgentDefinition.
 *
 * <p>An AgentProfile does not redefine the agent. It provides runtime
 * preferences, limits and environment-specific overrides.</p>
 */
public record AgentProfile(
        String id,
        String name,

        Set<String> enabledCapabilities,

        Map<String, Object> configuration,

        ExecutionLimits executionLimits,

        ModelPolicy modelPolicy,

        ResourcePolicy resourcePolicy,

        SecurityPolicy securityPolicy,

        Map<String, String> labels
) {

    public AgentProfile {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException(
                    "profile id cannot be blank");
        }

        name = name == null || name.isBlank()
                ? id
                : name;

        enabledCapabilities =
                enabledCapabilities == null
                        ? Set.of()
                        : Set.copyOf(enabledCapabilities);

        configuration =
                configuration == null
                        ? Map.of()
                        : Map.copyOf(configuration);

        labels =
                labels == null
                        ? Map.of()
                        : Map.copyOf(labels);
    }

    public static AgentProfile defaults(String id) {
        return new AgentProfile(
                id,
                id,
                Set.of(),
                Map.of(),
                ExecutionLimits.defaults(),
                ModelPolicy.defaults(),
                ResourcePolicy.defaults(),
                SecurityPolicy.defaults(),
                Map.of());
    }

    public boolean enables(String capability) {
        return enabledCapabilities.contains(capability);
    }

    public record ExecutionLimits(
            Duration maxDuration,
            int maxSteps,
            int maxToolCalls,
            long maxInputTokens,
            long maxOutputTokens
    ) {

        public ExecutionLimits {
            if (maxDuration == null) {
                maxDuration = Duration.ofMinutes(5);
            }

            if (maxSteps < 0) {
                maxSteps = 25;
            }

            if (maxToolCalls < 0) {
                maxToolCalls = 50;
            }

            if (maxInputTokens < 0) {
                maxInputTokens = 200_000;
            }

            if (maxOutputTokens < 0) {
                maxOutputTokens = 50_000;
            }
        }

        public static ExecutionLimits defaults() {
            return new ExecutionLimits(
                    Duration.ofMinutes(5),
                    25,
                    50,
                    200_000,
                    50_000);
        }
    }

    public record ModelPolicy(
            String strategy,
            String preferredModel,
            Set<String> allowedModels
    ) {

        public ModelPolicy {
            strategy =
                    strategy == null || strategy.isBlank()
                            ? "default"
                            : strategy;

            allowedModels =
                    allowedModels == null
                            ? Set.of()
                            : Set.copyOf(allowedModels);
        }

        public static ModelPolicy defaults() {
            return new ModelPolicy(
                    "default",
                    null,
                    Set.of());
        }
    }

    public record ResourcePolicy(
            boolean workspace,
            boolean network,
            boolean process,
            boolean filesystem
    ) {

        public static ResourcePolicy defaults() {
            return new ResourcePolicy(
                    true,
                    false,
                    false,
                    true);
        }
    }

    public record SecurityPolicy(
            boolean requireToolApproval,
            boolean audit,
            boolean failClosed
    ) {

        public static SecurityPolicy defaults() {
            return new SecurityPolicy(
                    false,
                    true,
                    true);
        }
    }
}
