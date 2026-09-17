package tech.kayys.wayang.harness.environment;

import tech.kayys.wayang.harness.context.HarnessIdentity;
import tech.kayys.wayang.harness.context.HarnessSession;

/**
 * Describes the complete agent operating environment within the Harness.
 */
public interface HarnessEnvironment {

    HarnessIdentity identity();

    HarnessSession session();

    HarnessCapabilities capabilities();

    HarnessResources resources();
}
