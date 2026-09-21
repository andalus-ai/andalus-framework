package tech.kayys.andalus.harness.runtime;

import tech.kayys.andalus.harness.context.HarnessContext;
import tech.kayys.andalus.harness.environment.HarnessEnvironment;
import tech.kayys.andalus.harness.governance.HarnessGovernance;
import tech.kayys.andalus.harness.lifecycle.HarnessLifecycle;

/**
 * Access point to the Harness environment, context, lifecycle, and governance for an active execution.
 */
public interface HarnessRuntime {

    HarnessEnvironment environment();

    HarnessContext context();

    HarnessLifecycle lifecycle();

    default HarnessGovernance governance() {
        return null;
    }
}
