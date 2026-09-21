package tech.kayys.andalus.harness.environment.v3;

import tech.kayys.andalus.harness.capability.CapabilityScope;

/**
 * High-level runtime view over the execution environment.
 */
public interface EnvironmentRuntime {

    AndalusEnvironment current();

    EnvironmentSnapshot snapshot();

    CapabilityScope capabilities();
}
