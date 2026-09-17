package tech.kayys.wayang.harness.runtime;

import tech.kayys.wayang.harness.context.HarnessContext;
import tech.kayys.wayang.harness.environment.HarnessEnvironment;
import tech.kayys.wayang.harness.lifecycle.HarnessLifecycle;

/**
 * Access point to the Harness environment, context, and lifecycle for an active execution.
 */
public interface HarnessRuntime {

    HarnessEnvironment environment();

    HarnessContext context();

    HarnessLifecycle lifecycle();
}
