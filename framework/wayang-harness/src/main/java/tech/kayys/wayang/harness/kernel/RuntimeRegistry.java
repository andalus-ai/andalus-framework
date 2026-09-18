package tech.kayys.wayang.harness.kernel;

import java.util.List;
import java.util.Optional;

/**
 * Registry for managing Wayang modules and resolving topological startup order.
 */
public interface RuntimeRegistry {

    void register(WayangModule module);

    Optional<WayangModule> get(ModuleId id);

    List<WayangModule> all();

    List<WayangModule> resolveStartupOrder();
}
