package tech.kayys.andalus.harness.planning;

import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Registry for {@link Planner} instances, keyed by {@link Planner#id()}.
 *
 * <p>Thread-safe. Planners are registered once at startup and looked up at
 * runtime by the {@link PlanningRuntime}.</p>
 */
public class PlannerRegistry {

    private final ConcurrentHashMap<String, Planner> planners = new ConcurrentHashMap<>();

    /** Registers a planner, replacing any previous entry with the same ID. */
    public void register(Planner planner) {
        if (planner == null) throw new IllegalArgumentException("planner must not be null");
        planners.put(planner.id(), planner);
    }

    /** Removes a planner by ID. Returns the removed planner, or empty if not found. */
    public Optional<Planner> unregister(String plannerId) {
        return Optional.ofNullable(planners.remove(plannerId));
    }

    /** Looks up a planner by ID. */
    public Optional<Planner> find(String plannerId) {
        return Optional.ofNullable(planners.get(plannerId));
    }

    /** Returns all registered planners. */
    public Collection<Planner> all() {
        return planners.values();
    }

    /** Returns true if a planner with the given ID is registered. */
    public boolean contains(String plannerId) {
        return planners.containsKey(plannerId);
    }

    /** Returns the number of registered planners. */
    public int size() {
        return planners.size();
    }
}
