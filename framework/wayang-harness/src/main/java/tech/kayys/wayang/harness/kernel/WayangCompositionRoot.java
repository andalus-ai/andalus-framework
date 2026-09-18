package tech.kayys.wayang.harness.kernel;

import java.util.*;

/**
 * Composition root for assembling and wiring Wayang Harness modules, services, and kernel.
 * This is the canonical factory for constructing fully configured Harness instances.
 */
public final class WayangCompositionRoot {

    private HarnessId harnessId = HarnessId.generate();
    private final List<WayangModule> modules = new ArrayList<>();
    private final ServiceRegistry services = new DefaultServiceRegistry();
    private final Map<String, Object> configuration = new HashMap<>();

    public static WayangCompositionRoot create() {
        return new WayangCompositionRoot();
    }

    public WayangCompositionRoot withId(HarnessId id) {
        this.harnessId = Objects.requireNonNull(id, "HarnessId cannot be null");
        return this;
    }

    public WayangCompositionRoot registerModule(WayangModule module) {
        this.modules.add(Objects.requireNonNull(module, "Module cannot be null"));
        return this;
    }

    public <T> WayangCompositionRoot registerService(ServiceKey<T> key, T service) {
        this.services.register(key, service);
        return this;
    }

    public <T> WayangCompositionRoot registerService(Class<T> type, T service) {
        this.services.register(type, service);
        return this;
    }

    public WayangCompositionRoot configure(String key, Object value) {
        this.configuration.put(key, value);
        return this;
    }

    public WayangCompositionRoot configureAll(Map<String, Object> config) {
        if (config != null) {
            this.configuration.putAll(config);
        }
        return this;
    }

    public HarnessKernel build() {
        RuntimeRegistry runtimes = new DefaultRuntimeRegistry();
        for (WayangModule module : modules) {
            runtimes.register(module);
        }

        LifecycleCoordinator lifecycle = new DefaultLifecycleCoordinator();

        return new DefaultHarnessKernel(
                harnessId,
                runtimes,
                services,
                lifecycle,
                configuration
        );
    }
}
