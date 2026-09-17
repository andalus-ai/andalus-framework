package tech.kayys.wayang.harness.environment;

import java.util.Optional;
import java.util.Set;

/**
 * SPI for locating and allocating resources in the agent's execution environment.
 */
public interface HarnessResources {

    Optional<ResourceHandle> find(ResourceId id);

    ResourceHandle require(ResourceId id);

    Set<ResourceId> available();
}
