package tech.kayys.wayang.harness.environment.v3;

import tech.kayys.wayang.harness.capability.CapabilityScope;

import java.util.Objects;

/**
 * Default implementation of {@link EnvironmentRuntime}.
 */
public class DefaultEnvironmentRuntime implements EnvironmentRuntime {

    private final WayangEnvironment environment;

    public DefaultEnvironmentRuntime(WayangEnvironment environment) {
        this.environment = Objects.requireNonNull(environment, "environment");
    }

    @Override
    public WayangEnvironment current() {
        return environment;
    }

    @Override
    public EnvironmentSnapshot snapshot() {
        return EnvironmentSnapshot.of(environment);
    }

    @Override
    public CapabilityScope capabilities() {
        return environment.capabilities();
    }
}
