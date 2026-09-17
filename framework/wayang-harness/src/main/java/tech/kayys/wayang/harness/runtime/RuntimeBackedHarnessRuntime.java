package tech.kayys.wayang.harness.runtime;

import tech.kayys.wayang.harness.context.HarnessContext;
import tech.kayys.wayang.harness.environment.HarnessEnvironment;
import tech.kayys.wayang.harness.governance.DefaultHarnessGovernance;
import tech.kayys.wayang.harness.governance.HarnessGovernance;
import tech.kayys.wayang.harness.lifecycle.HarnessLifecycle;

import java.util.Objects;

/**
 * Concrete {@link HarnessRuntime} backed by explicit environment, context, lifecycle, and governance instances.
 */
public final class RuntimeBackedHarnessRuntime implements HarnessRuntime {

    private final HarnessEnvironment environment;
    private final HarnessContext context;
    private final HarnessLifecycle lifecycle;
    private final HarnessGovernance governance;

    public RuntimeBackedHarnessRuntime(
            HarnessEnvironment environment,
            HarnessContext context,
            HarnessLifecycle lifecycle) {
        this(environment, context, lifecycle, new DefaultHarnessGovernance(environment.capabilities()));
    }

    public RuntimeBackedHarnessRuntime(
            HarnessEnvironment environment,
            HarnessContext context,
            HarnessLifecycle lifecycle,
            HarnessGovernance governance) {
        this.environment = Objects.requireNonNull(environment, "environment");
        this.context = Objects.requireNonNull(context, "context");
        this.lifecycle = Objects.requireNonNull(lifecycle, "lifecycle");
        this.governance = governance != null ? governance : new DefaultHarnessGovernance(environment.capabilities());
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

    @Override
    public HarnessGovernance governance() {
        return governance;
    }
}
