package tech.kayys.andalus.a2a.durable;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

/**
 * Default {@link A2AWakeCoordinator} (6.16.4.22).
 *
 * <p>Registration semantics:
 * <ol>
 *   <li>find task in the durable ledger</li>
 *   <li>verify the caller execution owns the task</li>
 *   <li>associate {@code taskId → callerExecutionId} transiently</li>
 * </ol>
 *
 * <p>Completion semantics: resolve the caller execution from the transient
 * registration, falling back to the durable ledger (registrations are lost on
 * restart; the ledger is not — 6.16.4.10), then request a wake via
 * {@link A2AExecutionWakeup}. The wake itself re-validates durable execution
 * state, so stale or duplicate signals are harmless.</p>
 */
public final class DefaultA2AWakeCoordinator implements A2AWakeCoordinator {

    private static final Logger LOG = Logger.getLogger(DefaultA2AWakeCoordinator.class.getName());

    private final DurableA2ATaskLedger ledger;
    private final A2AExecutionWakeup wakeup;

    /** taskId → callerExecutionId (transient; reconstructible from the ledger) */
    private final Map<String, String> registrations = new ConcurrentHashMap<>();

    public DefaultA2AWakeCoordinator(DurableA2ATaskLedger ledger, A2AExecutionWakeup wakeup) {
        this.ledger = Objects.requireNonNull(ledger, "ledger");
        this.wakeup = Objects.requireNonNull(wakeup, "wakeup");
    }

    @Override
    public void register(String callerExecutionId, String taskId) {
        Objects.requireNonNull(callerExecutionId, "callerExecutionId");
        Objects.requireNonNull(taskId, "taskId");

        DurableA2ATask task = ledger.find(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Unknown A2A task: " + taskId));

        if (!callerExecutionId.equals(task.callerExecutionId())) {
            throw new IllegalArgumentException(
                    "A2A task " + taskId + " belongs to execution " + task.callerExecutionId()
                            + ", not " + callerExecutionId);
        }

        registrations.put(taskId, callerExecutionId);
        LOG.fine(() -> "A2A wake registered: task=" + taskId + " caller=" + callerExecutionId);
    }

    @Override
    public void complete(String taskId) {
        wakeCaller(taskId, "completed");
    }

    @Override
    public void fail(String taskId) {
        wakeCaller(taskId, "failed");
    }

    @Override
    public void cancel(String taskId) {
        wakeCaller(taskId, "cancelled");
    }

    // ── Internal ──────────────────────────────────────────────────────────────

    private void wakeCaller(String taskId, String outcome) {
        String callerExecutionId = resolveCaller(taskId);
        if (callerExecutionId == null) {
            LOG.warning("Cannot wake for A2A task " + taskId + " (" + outcome
                    + "): task unknown to registration and ledger");
            return;
        }
        registrations.remove(taskId);
        LOG.fine(() -> "A2A task " + taskId + " " + outcome + " → wake " + callerExecutionId);
        wakeup.wake(callerExecutionId);
    }

    private String resolveCaller(String taskId) {
        String registered = registrations.get(taskId);
        if (registered != null) {
            return registered;
        }
        // Registration is transient; the durable ledger is the source of truth.
        Optional<DurableA2ATask> task = ledger.find(taskId);
        return task.map(DurableA2ATask::callerExecutionId).orElse(null);
    }
}
