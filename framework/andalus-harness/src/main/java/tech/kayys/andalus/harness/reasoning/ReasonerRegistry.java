package tech.kayys.andalus.harness.reasoning;

import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Registry for {@link ContextAwareReasoner} instances, keyed by {@link ContextAwareReasoner#id()}.
 *
 * <p>Thread-safe. Reasoners are registered once at startup and resolved by
 * the {@link ReasoningRuntime} when handling a {@link ReasoningRequest}.</p>
 */
public class ReasonerRegistry {

    private final ConcurrentHashMap<String, ContextAwareReasoner> reasoners = new ConcurrentHashMap<>();

    /** Registers a reasoner, replacing any previous entry with the same ID. */
    public void register(ContextAwareReasoner reasoner) {
        if (reasoner == null) throw new IllegalArgumentException("reasoner must not be null");
        reasoners.put(reasoner.id(), reasoner);
    }

    /** Removes a reasoner by ID. Returns the removed instance or empty. */
    public Optional<ContextAwareReasoner> unregister(String id) {
        return Optional.ofNullable(reasoners.remove(id));
    }

    /** Looks up a reasoner by ID. */
    public Optional<ContextAwareReasoner> find(String id) {
        return Optional.ofNullable(reasoners.get(id));
    }

    /**
     * Finds the first registered reasoner that supports the given purpose.
     * Returns empty if none is registered or none supports the purpose.
     */
    public Optional<ContextAwareReasoner> findFor(ReasoningPurpose purpose) {
        return reasoners.values().stream()
                .filter(r -> r.supports(purpose))
                .findFirst();
    }

    /** Returns all registered reasoners. */
    public Collection<ContextAwareReasoner> all() {
        return reasoners.values();
    }

    /** Returns true if any reasoner is registered. */
    public boolean isEmpty() {
        return reasoners.isEmpty();
    }

    /** Returns the number of registered reasoners. */
    public int size() {
        return reasoners.size();
    }
}
