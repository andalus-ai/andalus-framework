package tech.kayys.andalus.harness.kernel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Standard implementation of {@link LifecycleCoordinator}.
 * Starts in dependency order and stops/destroys in reverse dependency order.
 */
public class DefaultLifecycleCoordinator implements LifecycleCoordinator {

    @Override
    public void initializeAll(List<AndalusModule> modules, ServiceRegistry services, Map<String, Object> configuration) {
        Objects.requireNonNull(modules, "modules cannot be null");
        Objects.requireNonNull(services, "services cannot be null");
        Map<String, Object> config = configuration != null ? configuration : Map.of();

        for (AndalusModule module : modules) {
            ModuleContext context = new ModuleContext() {
                @Override
                public ModuleId moduleId() {
                    return module.id();
                }

                @Override
                public ServiceRegistry services() {
                    return services;
                }

                @Override
                public Map<String, Object> configuration() {
                    return config;
                }
            };
            module.initialize(context);
        }
    }

    @Override
    public void startAll(List<AndalusModule> startupOrder) {
        Objects.requireNonNull(startupOrder, "startupOrder cannot be null");
        for (AndalusModule module : startupOrder) {
            module.start();
        }
    }

    @Override
    public void stopAll(List<AndalusModule> startupOrder) {
        Objects.requireNonNull(startupOrder, "startupOrder cannot be null");
        List<AndalusModule> reverseOrder = new ArrayList<>(startupOrder);
        Collections.reverse(reverseOrder);
        for (AndalusModule module : reverseOrder) {
            try {
                module.stop();
            } catch (Exception e) {
                // Log or collect error but proceed with shutting down remaining modules
            }
        }
    }

    @Override
    public void destroyAll(List<AndalusModule> startupOrder) {
        Objects.requireNonNull(startupOrder, "startupOrder cannot be null");
        List<AndalusModule> reverseOrder = new ArrayList<>(startupOrder);
        Collections.reverse(reverseOrder);
        for (AndalusModule module : reverseOrder) {
            try {
                module.destroy();
            } catch (Exception e) {
                // Ignore during teardown
            }
        }
    }
}
