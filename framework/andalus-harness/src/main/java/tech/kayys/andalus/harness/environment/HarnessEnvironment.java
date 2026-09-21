package tech.kayys.andalus.harness.environment;

import tech.kayys.andalus.harness.context.HarnessIdentity;
import tech.kayys.andalus.harness.context.HarnessSession;

/**
 * Describes the complete agent operating environment within the Harness.
 */
public interface HarnessEnvironment {

    HarnessIdentity identity();

    HarnessSession session();

    HarnessCapabilities capabilities();

    HarnessResources resources();
}
