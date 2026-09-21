package tech.kayys.andalus.harness.environment.v3;

import tech.kayys.andalus.harness.capability.CapabilityScope;

import java.util.Objects;

/**
 * Default implementation of {@link EnvironmentRuntime}.
 */
public class DefaultEnvironmentRuntime implements EnvironmentRuntime {

    private final AndalusEnvironment environment;

    public DefaultEnvironmentRuntime(AndalusEnvironment environment) {
        this.environment = Objects.requireNonNull(environment, "environment");
    }

    @Override
    public AndalusEnvironment current() {
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
