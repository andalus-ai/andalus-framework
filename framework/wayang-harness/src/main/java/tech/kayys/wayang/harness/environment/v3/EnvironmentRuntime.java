package tech.kayys.wayang.harness.environment.v3;

import tech.kayys.wayang.harness.capability.CapabilityScope;

/**
 * High-level runtime view over the execution environment.
 */
public interface EnvironmentRuntime {

    WayangEnvironment current();

    EnvironmentSnapshot snapshot();

    CapabilityScope capabilities();
}
