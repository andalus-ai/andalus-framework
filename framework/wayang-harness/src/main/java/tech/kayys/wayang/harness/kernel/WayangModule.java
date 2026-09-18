package tech.kayys.wayang.harness.kernel;

import java.util.Set;

/**
 * Standard lifecycle abstraction for all Wayang subsystems and components.
 */
public interface WayangModule {

    ModuleId id();

    ModuleVersion version();

    default Set<ModuleDependency> dependencies() {
        return descriptor().dependencies();
    }

    ModuleDescriptor descriptor();

    void initialize(ModuleContext context);

    void start();

    void stop();

    void destroy();
}
