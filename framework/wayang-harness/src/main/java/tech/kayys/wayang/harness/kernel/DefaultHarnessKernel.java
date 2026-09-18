package tech.kayys.wayang.harness.kernel;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Standard implementation of {@link HarnessKernel}.
 */
public class DefaultHarnessKernel implements HarnessKernel {

    private final HarnessId id;
    private final AtomicReference<HarnessState> state = new AtomicReference<>(HarnessState.CREATED);
    private final RuntimeRegistry runtimes;
    private final ServiceRegistry services;
    private final LifecycleCoordinator lifecycle;
    private final Map<String, Object> configuration;

    private List<WayangModule> resolvedStartupOrder = List.of();

    public DefaultHarnessKernel(
            HarnessId id,
            RuntimeRegistry runtimes,
            ServiceRegistry services,
            LifecycleCoordinator lifecycle,
            Map<String, Object> configuration
    ) {
        this.id = Objects.requireNonNull(id, "HarnessId cannot be null");
        this.runtimes = Objects.requireNonNull(runtimes, "RuntimeRegistry cannot be null");
        this.services = Objects.requireNonNull(services, "ServiceRegistry cannot be null");
        this.lifecycle = Objects.requireNonNull(lifecycle, "LifecycleCoordinator cannot be null");
        this.configuration = configuration != null ? Map.copyOf(configuration) : Map.of();
    }

    @Override
    public HarnessId id() {
        return id;
    }

    @Override
    public HarnessState state() {
        return state.get();
    }

    @Override
    public RuntimeRegistry runtimes() {
        return runtimes;
    }

    @Override
    public ServiceRegistry services() {
        return services;
    }

    @Override
    public LifecycleCoordinator lifecycle() {
        return lifecycle;
    }

    @Override
    public synchronized void initialize() {
        if (!state.compareAndSet(HarnessState.CREATED, HarnessState.INITIALIZING)) {
            throw new IllegalStateException("Cannot initialize kernel in state: " + state.get());
        }

        try {
            this.resolvedStartupOrder = runtimes.resolveStartupOrder();
            lifecycle.initializeAll(resolvedStartupOrder, services, configuration);
            state.set(HarnessState.INITIALIZED);
        } catch (Exception e) {
            state.set(HarnessState.FAILED);
            throw new RuntimeException("Kernel initialization failed", e);
        }
    }

    @Override
    public synchronized void start() {
        if (!state.compareAndSet(HarnessState.INITIALIZED, HarnessState.STARTING)) {
            throw new IllegalStateException("Cannot start kernel in state: " + state.get());
        }

        try {
            lifecycle.startAll(resolvedStartupOrder);
            state.set(HarnessState.RUNNING);
        } catch (Exception e) {
            state.set(HarnessState.FAILED);
            throw new RuntimeException("Kernel startup failed", e);
        }
    }

    @Override
    public synchronized void stop() {
        HarnessState current = state.get();
        if (current != HarnessState.RUNNING && current != HarnessState.READY) {
            return;
        }

        state.set(HarnessState.STOPPING);
        try {
            lifecycle.stopAll(resolvedStartupOrder);
            state.set(HarnessState.STOPPED);
        } catch (Exception e) {
            state.set(HarnessState.FAILED);
            throw new RuntimeException("Kernel stop failed", e);
        }
    }

    @Override
    public synchronized void destroy() {
        stop();
        lifecycle.destroyAll(resolvedStartupOrder);
    }
}
