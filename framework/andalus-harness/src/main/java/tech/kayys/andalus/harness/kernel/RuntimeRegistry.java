package tech.kayys.andalus.harness.kernel;

import java.util.List;
import java.util.Optional;

/**
 * Registry for managing Andalus modules and resolving topological startup order.
 */
public interface RuntimeRegistry {

    void register(AndalusModule module);

    Optional<AndalusModule> get(ModuleId id);

    List<AndalusModule> all();

    List<AndalusModule> resolveStartupOrder();
}
