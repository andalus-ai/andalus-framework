package tech.kayys.andalus.a2a.durable;

import tech.kayys.andalus.a2a.model.A2AArtifact;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Logger;

/**
 * Default {@link A2ACompletionBridge} (6.16.5.9).
 *
 * <p>Improvement over the conceptual design: the bridge delegates the wake to the
 * {@link A2AWakeCoordinator} so that caller resolution (transient registration with
 * durable-ledger fallback) exists in exactly one place.</p>
 *
 * <p>Ordering guarantee (6.16.5.2 / 6.16.5.14):</p>
 * <pre>
 * remote terminal signal
 *      ↓
 * persist task result + terminal state   (durable handoff)
 *      ↓
 * wake(local callerExecutionId)          (validated against durable state)
 * </pre>
 */
public final class DefaultA2ACompletionBridge implements A2ACompletionBridge {

    private static final Logger LOG = Logger.getLogger(DefaultA2ACompletionBridge.class.getName());

    private final DurableA2ATaskLedger ledger;
    private final A2AWakeCoordinator wakeCoordinator;

    public DefaultA2ACompletionBridge(DurableA2ATaskLedger ledger, A2AWakeCoordinator wakeCoordinator) {
        this.ledger = Objects.requireNonNull(ledger, "ledger");
        this.wakeCoordinator = Objects.requireNonNull(wakeCoordinator, "wakeCoordinator");
    }

    @Override
    public void completed(String taskId) {
        completed(taskId, List.of(), Map.of());
    }

    @Override
    public void completed(String taskId, List<A2AArtifact> artifacts, Map<String, Object> metadata) {
        DurableA2ATask task = requireTask(taskId);

        if (!task.isTerminal()) {
            // Persist result envelope BEFORE the wake (6.16.5.14)
            ledger.complete(taskId, artifacts, metadata);
        } else {
            LOG.fine(() -> "Duplicate completion for terminal A2A task " + taskId
                    + " (" + task.status() + ") — not transitioning again");
        }

        // Wake is duplicate-safe: the local wake path reloads durable state (6.16.5.16)
        wakeCoordinator.complete(taskId);
    }

    @Override
    public void failed(String taskId, String reason) {
        DurableA2ATask task = requireTask(taskId);

        if (!task.isTerminal()) {
            ledger.fail(taskId, reason);
        } else {
            LOG.fine(() -> "Duplicate failure for terminal A2A task " + taskId
                    + " (" + task.status() + ") — not transitioning again");
        }

        wakeCoordinator.fail(taskId);
    }

    @Override
    public void cancelled(String taskId) {
        DurableA2ATask task = requireTask(taskId);

        if (!task.isTerminal()) {
            ledger.cancel(taskId);
        } else {
            LOG.fine(() -> "Duplicate cancellation for terminal A2A task " + taskId
                    + " (" + task.status() + ") — not transitioning again");
        }

        wakeCoordinator.cancel(taskId);
    }

    private DurableA2ATask requireTask(String taskId) {
        return ledger.find(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Unknown A2A task: " + taskId));
    }
}
