package tech.kayys.wayang.harness.runtime;

import tech.kayys.wayang.harness.context.HarnessContext;
import tech.kayys.wayang.harness.environment.HarnessEnvironment;
import tech.kayys.wayang.harness.lifecycle.HarnessLifecycle;

import java.util.Objects;

/**
 * Concrete {@link HarnessRuntime} backed by explicit environment, context, and lifecycle instances.
 */
public final class RuntimeBackedHarnessRuntime implements HarnessRuntime {

    private final HarnessEnvironment environment;
    private final HarnessContext context;
    private final HarnessLifecycle lifecycle;

    public RuntimeBackedHarnessRuntime(
            HarnessEnvironment environment,
            HarnessContext context,
            HarnessLifecycle lifecycle) {
        this.environment = Objects.requireNonNull(environment, "environment");
        this.context = Objects.requireNonNull(context, "context");
        this.lifecycle = Objects.requireNonNull(lifecycle, "lifecycle");
    }

    @Override
    public HarnessEnvironment environment() {
        return environment;
    }

    @Override
    public HarnessContext context() {
        return context;
    }

    @Override
    public HarnessLifecycle lifecycle() {
        return lifecycle;
    }
}
