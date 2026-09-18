package tech.kayys.wayang.harness.environment.v3;

import tech.kayys.wayang.harness.capability.CapabilityScope;
import tech.kayys.wayang.harness.environment.v3.resource.ResourceCatalog;

/**
 * Core contract for an execution substrate in Wayang Harness v3.0.
 */
public interface WayangEnvironment {

    EnvironmentId id();

    EnvironmentDescriptor descriptor();

    ResourceCatalog resources();

    CapabilityScope capabilities();

    EnvironmentState state();
}
