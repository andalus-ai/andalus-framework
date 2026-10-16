package tech.kayys.andalus.skill.lifecycle;

import tech.kayys.andalus.skill.spi.SkillReference;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * In-memory {@link SkillLifecycleManager} implementation.
 *
 * <p>Suitable for single-process deployments and tests. State is not
 * persisted across JVM restarts. For durable lifecycle management
 * (marketplace, enterprise policy), provide a custom implementation
 * backed by a persistent store.</p>
 */
public final class InMemorySkillLifecycleManager implements SkillLifecycleManager {

    private final ConcurrentMap<String, SkillLifecycleState> states =
            new ConcurrentHashMap<>();

    private final List<SkillLifecycleListener> listeners =
            new CopyOnWriteArrayList<>();

    /**
     * Register a lifecycle listener that is notified synchronously on each
     * transition. Listener exceptions are silently swallowed to protect other
     * listeners.
     */
    public void addListener(SkillLifecycleListener listener) {
        if (listener != null) {
            listeners.add(listener);
        }
    }

    /** Remove a previously registered listener. */
    public void removeListener(SkillLifecycleListener listener) {
        listeners.remove(listener);
    }

    @Override
    public CompletionStage<SkillLifecycleState> activate(SkillReference reference) {
        return transition(reference, SkillLifecycleState.ACTIVE, "activated");
    }

    @Override
    public CompletionStage<SkillLifecycleState> deactivate(SkillReference reference) {
        return transition(reference, SkillLifecycleState.DISABLED, "deactivated");
    }

    @Override
    public CompletionStage<Void> uninstall(SkillReference reference) {
        transition(reference, SkillLifecycleState.UNINSTALLED, "uninstalled");
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public SkillLifecycleState currentState(SkillReference reference) {
        return states.getOrDefault(
                reference.id(), SkillLifecycleState.DISCOVERED);
    }

    // ── internals ────────────────────────────────────────────────────────────

    private CompletionStage<SkillLifecycleState> transition(
            SkillReference reference,
            SkillLifecycleState newState,
            String reason) {

        SkillLifecycleState previous =
                states.put(reference.id(), newState);

        SkillLifecycleEvent event =
                SkillLifecycleEvent.of(reference, previous, newState, reason);

        notifyListeners(event);
        return CompletableFuture.completedFuture(newState);
    }

    private void notifyListeners(SkillLifecycleEvent event) {
        for (SkillLifecycleListener listener : listeners) {
            try {
                listener.onLifecycleEvent(event);
            } catch (Exception ignored) {
                // Listener failures must not interrupt lifecycle management
            }
        }
    }
}
