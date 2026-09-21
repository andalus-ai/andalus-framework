package tech.kayys.andalus.harness.kernel;

import java.util.Set;

/**
 * Standard lifecycle abstraction for all Andalus subsystems and components.
 */
public interface AndalusModule {

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
