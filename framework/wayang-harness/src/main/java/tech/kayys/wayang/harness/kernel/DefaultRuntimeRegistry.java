package tech.kayys.wayang.harness.kernel;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Thread-safe reference implementation of {@link RuntimeRegistry} with DAG topological sorting.
 */
public class DefaultRuntimeRegistry implements RuntimeRegistry {

    private final Map<ModuleId, WayangModule> modules = new ConcurrentHashMap<>();

    @Override
    public void register(WayangModule module) {
        Objects.requireNonNull(module, "WayangModule cannot be null");
        modules.put(module.id(), module);
    }

    @Override
    public Optional<WayangModule> get(ModuleId id) {
        Objects.requireNonNull(id, "ModuleId cannot be null");
        return Optional.ofNullable(modules.get(id));
    }

    @Override
    public List<WayangModule> all() {
        return List.copyOf(modules.values());
    }

    @Override
    public List<WayangModule> resolveStartupOrder() {
        // Validate required dependencies exist and versions match
        for (WayangModule module : modules.values()) {
            for (ModuleDependency dep : module.dependencies()) {
                WayangModule resolvedDep = modules.get(dep.id());
                if (resolvedDep == null) {
                    if (!dep.optional()) {
                        throw new IllegalStateException("Missing required module dependency: "
                                + dep.id().value() + " for module " + module.id().value());
                    }
                } else {
                    if (!resolvedDep.version().isCompatibleWith(dep.minVersion())) {
                        throw new IllegalStateException("Incompatible version for module dependency "
                                + dep.id().value() + ": expected at least " + dep.minVersion()
                                + ", but got " + resolvedDep.version());
                    }
                }
            }
        }

        // Topological Sort (DFS with cycle detection)
        // Visited state: 0 = unvisited, 1 = visiting (grey), 2 = visited (black)
        Map<ModuleId, Integer> visitState = new HashMap<>();
        List<WayangModule> result = new ArrayList<>();

        for (ModuleId moduleId : modules.keySet()) {
            if (visitState.getOrDefault(moduleId, 0) == 0) {
                dfs(moduleId, visitState, result);
            }
        }

        return Collections.unmodifiableList(result);
    }

    private void dfs(ModuleId moduleId, Map<ModuleId, Integer> visitState, List<WayangModule> result) {
        visitState.put(moduleId, 1); // visiting

        WayangModule module = modules.get(moduleId);
        if (module != null) {
            for (ModuleDependency dep : module.dependencies()) {
                if (modules.containsKey(dep.id())) {
                    int state = visitState.getOrDefault(dep.id(), 0);
                    if (state == 1) {
                        throw new IllegalStateException("Circular dependency detected involving module: "
                                + dep.id().value() + " and " + moduleId.value());
                    } else if (state == 0) {
                        dfs(dep.id(), visitState, result);
                    }
                }
            }
        }

        visitState.put(moduleId, 2); // visited
        if (module != null) {
            result.add(module);
        }
    }
}
