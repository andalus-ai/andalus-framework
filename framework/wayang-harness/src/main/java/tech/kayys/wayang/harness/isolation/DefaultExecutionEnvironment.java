package tech.kayys.wayang.harness.isolation;

import java.util.Objects;

/**
 * Immutable reference record implementing {@link ExecutionEnvironment}.
 */
public record DefaultExecutionEnvironment(
        EnvironmentId id,
        EnvironmentType type,
        EnvironmentCapabilities capabilities,
        EnvironmentIsolation isolation
) implements ExecutionEnvironment {

    public DefaultExecutionEnvironment {
        Objects.requireNonNull(id, "EnvironmentId cannot be null");
        Objects.requireNonNull(type, "EnvironmentType cannot be null");
        capabilities = capabilities != null ? capabilities : EnvironmentCapabilities.defaults();
        isolation = isolation != null ? isolation : EnvironmentIsolation.unconstrained();
    }

    public static DefaultExecutionEnvironment inProcess() {
        return new DefaultExecutionEnvironment(
                EnvironmentId.of("in-process"),
                EnvironmentType.IN_PROCESS,
                EnvironmentCapabilities.defaults(),
                EnvironmentIsolation.unconstrained()
        );
    }

    public static DefaultExecutionEnvironment sandboxed(EnvironmentId id) {
        return new DefaultExecutionEnvironment(
                id,
                EnvironmentType.SANDBOX,
                EnvironmentCapabilities.defaults(),
                EnvironmentIsolation.sandboxed()
        );
    }
}
