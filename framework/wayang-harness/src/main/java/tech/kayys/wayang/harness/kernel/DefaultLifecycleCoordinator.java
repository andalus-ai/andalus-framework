package tech.kayys.wayang.harness.kernel;

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
    public void initializeAll(List<WayangModule> modules, ServiceRegistry services, Map<String, Object> configuration) {
        Objects.requireNonNull(modules, "modules cannot be null");
        Objects.requireNonNull(services, "services cannot be null");
        Map<String, Object> config = configuration != null ? configuration : Map.of();

        for (WayangModule module : modules) {
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
    public void startAll(List<WayangModule> startupOrder) {
        Objects.requireNonNull(startupOrder, "startupOrder cannot be null");
        for (WayangModule module : startupOrder) {
            module.start();
        }
    }

    @Override
    public void stopAll(List<WayangModule> startupOrder) {
        Objects.requireNonNull(startupOrder, "startupOrder cannot be null");
        List<WayangModule> reverseOrder = new ArrayList<>(startupOrder);
        Collections.reverse(reverseOrder);
        for (WayangModule module : reverseOrder) {
            try {
                module.stop();
            } catch (Exception e) {
                // Log or collect error but proceed with shutting down remaining modules
            }
        }
    }

    @Override
    public void destroyAll(List<WayangModule> startupOrder) {
        Objects.requireNonNull(startupOrder, "startupOrder cannot be null");
        List<WayangModule> reverseOrder = new ArrayList<>(startupOrder);
        Collections.reverse(reverseOrder);
        for (WayangModule module : reverseOrder) {
            try {
                module.destroy();
            } catch (Exception e) {
                // Ignore during teardown
            }
        }
    }
}
