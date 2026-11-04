package tech.kayys.andalus.harness.planning;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Registry for {@link Planner} instances, keyed by {@link Planner#id()}.
 *
 * <p>Thread-safe. Planners are registered once at startup and looked up at
 * runtime by the {@link PlanningRuntime}. Enumeration is ordered by planner
 * ID so callers never depend on {@link ConcurrentHashMap} iteration order.</p>
 */
public class PlannerRegistry {

    private final ConcurrentHashMap<String, Planner> planners = new ConcurrentHashMap<>();

    /** Registers a planner, replacing any previous entry with the same ID. */
    public void register(Planner planner) {
        Objects.requireNonNull(planner, "planner must not be null");
        String plannerId = planner.id();
        if (plannerId == null || plannerId.isBlank()) {
            throw new IllegalArgumentException("planner id must not be blank");
        }
        planners.put(plannerId, planner);
    }

    /** Removes a planner by ID. Returns the removed planner, or empty if not found. */
    public Optional<Planner> unregister(String plannerId) {
        return Optional.ofNullable(planners.remove(requireId(plannerId)));
    }

    /** Looks up a planner by ID. */
    public Optional<Planner> find(String plannerId) {
        return Optional.ofNullable(planners.get(requireId(plannerId)));
    }

    /**
     * Returns an immutable snapshot of registered planners ordered by ID.
     *
     * <p>The returned collection does not change when the registry is later
     * modified.</p>
     */
    public Collection<Planner> all() {
        return planners.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(Map.Entry::getValue)
                .collect(Collectors.toUnmodifiableList());
    }

    /** Returns true if a planner with the given ID is registered. */
    public boolean contains(String plannerId) {
        return planners.containsKey(requireId(plannerId));
    }

    /** Returns the number of registered planners. */
    public int size() {
        return planners.size();
    }

    private static String requireId(String plannerId) {
        if (plannerId == null || plannerId.isBlank()) {
            throw new IllegalArgumentException("planner id must not be blank");
        }
        return plannerId;
    }
}
