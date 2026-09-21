package tech.kayys.andalus.harness.environment.v3;

import tech.kayys.andalus.harness.capability.CapabilityScope;
import tech.kayys.andalus.harness.environment.v3.resource.ResourceCatalog;

/**
 * Core contract for an execution substrate in Andalus Harness v3.0.
 */
public interface AndalusEnvironment {

    EnvironmentId id();

    EnvironmentDescriptor descriptor();

    ResourceCatalog resources();

    CapabilityScope capabilities();

    EnvironmentState state();
}
