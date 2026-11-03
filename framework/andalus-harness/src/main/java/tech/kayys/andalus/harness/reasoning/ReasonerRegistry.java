package tech.kayys.andalus.harness.reasoning;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Registry for {@link ContextAwareReasoner} instances, keyed by
 * {@link ContextAwareReasoner#id()}.
 *
 * <p>Thread-safe. Reasoners are registered once at startup and resolved by
 * the {@link ReasoningRuntime} when handling a {@link ReasoningRequest}.
 * When more than one provider supports a purpose, selection is deterministic:
 * the provider with the lexicographically smallest stable ID is selected.
 * Callers that need a different policy should select by ID explicitly.</p>
 */
public class ReasonerRegistry {

    private final ConcurrentHashMap<String, ContextAwareReasoner> reasoners = new ConcurrentHashMap<>();

    /**
     * Registers a reasoner, replacing any previous entry with the same ID.
     *
     * @throws IllegalArgumentException if the reasoner or its ID is null/blank
     */
    public void register(ContextAwareReasoner reasoner) {
        Objects.requireNonNull(reasoner, "reasoner must not be null");
        String id = reasoner.id();
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("reasoner ID must not be blank");
        }
        reasoners.put(id, reasoner);
    }

    /** Removes a reasoner by ID. Returns the removed instance or empty. */
    public Optional<ContextAwareReasoner> unregister(String id) {
        return Optional.ofNullable(reasoners.remove(Objects.requireNonNull(id, "id must not be null")));
    }

    /** Looks up a reasoner by ID. */
    public Optional<ContextAwareReasoner> find(String id) {
        return Optional.ofNullable(reasoners.get(Objects.requireNonNull(id, "id must not be null")));
    }

    /**
     * Finds the first registered reasoner that supports the given purpose.
     * Selection is stable across JVM runs and independent of registration order.
     * Returns empty if no registered reasoner supports the purpose.
     */
    public Optional<ContextAwareReasoner> findFor(ReasoningPurpose purpose) {
        Objects.requireNonNull(purpose, "purpose must not be null");
        return reasoners.values().stream()
                .filter(reasoner -> reasoner.supports(purpose))
                .min(Comparator.comparing(ContextAwareReasoner::id));
    }

    /**
     * Returns an immutable snapshot of registered reasoners ordered by ID.
     * The returned collection does not change when the registry is modified.
     */
    public Collection<ContextAwareReasoner> all() {
        return reasoners.values().stream()
                .sorted(Comparator.comparing(ContextAwareReasoner::id))
                .toList();
    }

    /** Returns true if no reasoner is registered. */
    public boolean isEmpty() {
        return reasoners.isEmpty();
    }

    /** Returns the number of registered reasoners. */
    public int size() {
        return reasoners.size();
    }
}
