package tech.kayys.andalus.harness.kernel;

import java.util.*;

/**
 * Composition root for assembling and wiring Andalus Harness modules, services, and kernel.
 * This is the canonical factory for constructing fully configured Harness instances.
 */
public final class AndalusCompositionRoot {

    private HarnessId harnessId = HarnessId.generate();
    private final List<AndalusModule> modules = new ArrayList<>();
    private final ServiceRegistry services = new DefaultServiceRegistry();
    private final Map<String, Object> configuration = new HashMap<>();

    public static AndalusCompositionRoot create() {
        return new AndalusCompositionRoot();
    }

    public AndalusCompositionRoot withId(HarnessId id) {
        this.harnessId = Objects.requireNonNull(id, "HarnessId cannot be null");
        return this;
    }

    public AndalusCompositionRoot registerModule(AndalusModule module) {
        this.modules.add(Objects.requireNonNull(module, "Module cannot be null"));
        return this;
    }

    public <T> AndalusCompositionRoot registerService(ServiceKey<T> key, T service) {
        this.services.register(key, service);
        return this;
    }

    public <T> AndalusCompositionRoot registerService(Class<T> type, T service) {
        this.services.register(type, service);
        return this;
    }

    public AndalusCompositionRoot configure(String key, Object value) {
        this.configuration.put(key, value);
        return this;
    }

    public AndalusCompositionRoot configureAll(Map<String, Object> config) {
        if (config != null) {
            this.configuration.putAll(config);
        }
        return this;
    }

    public HarnessKernel build() {
        RuntimeRegistry runtimes = new DefaultRuntimeRegistry();
        for (AndalusModule module : modules) {
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
