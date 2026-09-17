package tech.kayys.wayang.harness.environment;

import java.util.Set;

/**
 * SPI for querying and requiring capabilities available to an agent in the Harness.
 */
public interface HarnessCapabilities {

    boolean has(CapabilityId capability);

    void require(CapabilityId capability);

    Set<CapabilityId> available();
}
