package tech.kayys.andalus.harness.execution.recovery;

import tech.kayys.andalus.harness.execution.action.ActionRecord;
import tech.kayys.andalus.harness.execution.state.ExecutionState;
import tech.kayys.andalus.harness.execution.state.ExecutionStatus;

import java.util.List;
import java.util.Objects;

/**
 * Conservative recovery strategy preventing blind replay of external side effects.
 *
 * <p>Decision matrix (in priority order):
 * <ol>
 *   <li>No prior state → FAIL</li>
 *   <li>Status is terminal → FAIL (already resolved)</li>
 *   <li>Status is SUSPENDED or WAITING → RESUME (was parked safely)</li>
 *   <li>Status is RECOVERING → delegate to {@link CrashRecoveryAnalyzer} to inspect in-flight actions</li>
 *   <li>Transient failure (timeout/connection) → RETRY</li>
 *   <li>In-flight STARTED actions detected → delegate to {@link CrashRecoveryAnalyzer}</li>
 *   <li>Everything else → WAIT (conservative: require operator confirmation)</li>
 * </ol>
 */
public class DefaultRecoveryStrategy implements RecoveryStrategy {

    private final CrashRecoveryAnalyzer crashAnalyzer;

    public DefaultRecoveryStrategy() {
        this(new CrashRecoveryAnalyzer());
    }

    public DefaultRecoveryStrategy(CrashRecoveryAnalyzer crashAnalyzer) {
        this.crashAnalyzer = Objects.requireNonNull(crashAnalyzer, "crashAnalyzer");
    }

    @Override
    public RecoveryDecision recover(ExecutionState state, RecoveryContext context) {
        Objects.requireNonNull(context, "context");

        if (state == null) {
            return RecoveryDecision.fail("No prior execution state found to recover from");
        }

        // Already terminal — do not restart
        if (state.status().isTerminal()) {
            return RecoveryDecision.fail("Execution is already in terminal state: " + state.status());
        }

        // Safely parked before crash — can resume from persisted cursor
        if (state.status() == ExecutionStatus.SUSPENDED || state.status() == ExecutionStatus.WAITING) {
            return RecoveryDecision.resume(
                    "Execution was safely suspended (" + state.status() + "); resuming from cursor " + state.cursor().phaseId());
        }

        // If the execution was mid-recovery when the second crash happened, re-analyze actions
        if (state.status() == ExecutionStatus.RECOVERING) {
            List<ActionRecord> history = actionHistory(context);
            return crashAnalyzer.analyze(history);
        }

        // Transient failure hint from the exception message
        if (context.failure().isPresent()) {
            Throwable err = context.failure().get();
            String msg = err.getMessage() != null ? err.getMessage().toLowerCase() : "";
            if (msg.contains("timeout") || msg.contains("connection") || msg.contains("retryable")) {
                return RecoveryDecision.retry("Transient failure detected: " + err.getMessage());
            }
        }

        // Inspect in-flight actions (STARTED status) per 6.13 — STARTED ≠ FAILED
        List<ActionRecord> history = actionHistory(context);
        boolean hasInFlight = history.stream()
                .anyMatch(a -> a.status() == tech.kayys.andalus.harness.execution.action.ActionStatus.STARTED);

        if (hasInFlight) {
            return crashAnalyzer.analyze(history);
        }

        return RecoveryDecision.waitAction(
                "Side-effect boundary reached or uncertain state: requires manual verification before replay");
    }

    /** Extract action history from context metadata if provided by the coordinator. */
    @SuppressWarnings("unchecked")
    private List<ActionRecord> actionHistory(RecoveryContext context) {
        Object raw = context.metadata().get("actionHistory");
        if (raw instanceof List<?> list) {
            return (List<ActionRecord>) list;
        }
        return List.of();
    }
}
