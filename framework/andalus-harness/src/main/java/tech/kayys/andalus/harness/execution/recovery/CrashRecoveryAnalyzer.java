package tech.kayys.andalus.harness.execution.recovery;

import tech.kayys.andalus.harness.execution.action.ActionExecutionMode;
import tech.kayys.andalus.harness.execution.action.ActionRecord;
import tech.kayys.andalus.harness.execution.action.ActionStatus;

import java.util.List;

/**
 * Analyzes action records after a process crash to classify the appropriate
 * recovery behavior for each action that was left in {@link ActionStatus#STARTED}.
 *
 * <p>A STARTED action after a crash means "outcome UNKNOWN" — the remote side may or
 * may not have committed the effect. The classification drives the
 * {@link RecoveryDecision} returned by {@link DefaultRecoveryStrategy}:
 *
 * <ul>
 *   <li>{@code PURE / IDEMPOTENT} → safe to replay (RETRY)</li>
 *   <li>{@code AT_LEAST_ONCE} → replay with stable idempotency key (RETRY)</li>
 *   <li>{@code AT_MOST_ONCE / NON_REPLAYABLE} → requires operator confirmation (WAIT)</li>
 *   <li>Any action in {@link ActionExecutionMode} → defaults to WAIT for unknown modes</li>
 * </ul>
 *
 * <p>This is intentionally NOT a recovery manager — it is a pure side-effect classifier
 * that returns the correct {@link RecoveryDecision} for the crash scenario.
 */
public class CrashRecoveryAnalyzer {

    /**
     * Returns a {@link RecoveryDecision} for the given execution's action history
     * when a crash has been detected (i.e. the previous execution terminated abnormally).
     *
     * <p>If no actions were in-flight (STARTED), recovery can safely resume or retry.
     * If in-flight actions exist, each one's {@link ActionExecutionMode} determines
     * whether the action is safe to replay or requires human reconciliation.
     *
     * @param actionHistory all action records for the execution
     * @return the most conservative recovery decision across all STARTED actions
     */
    public RecoveryDecision analyze(List<ActionRecord> actionHistory) {
        if (actionHistory == null || actionHistory.isEmpty()) {
            return RecoveryDecision.resume("No actions recorded; crash occurred before first action — safe to resume");
        }

        // Find actions that were STARTED (in-flight) when crash occurred
        List<ActionRecord> inFlight = actionHistory.stream()
                .filter(a -> a.status() == ActionStatus.STARTED)
                .toList();

        if (inFlight.isEmpty()) {
            return RecoveryDecision.resume("All recorded actions completed before crash — safe to resume");
        }

        // Evaluate each STARTED action — take the most conservative decision
        boolean requiresWait = false;
        boolean requiresCompensate = false;
        StringBuilder reason = new StringBuilder();

        for (ActionRecord action : inFlight) {
            reason.append("[").append(action.id().value()).append(":").append(action.type()).append("] ");
            switch (action.mode()) {
                case PURE, IDEMPOTENT, AT_LEAST_ONCE -> {
                    reason.append("outcome=UNKNOWN mode=").append(action.mode()).append(" → replay safe; ");
                }
                case AT_MOST_ONCE -> {
                    requiresWait = true;
                    reason.append("outcome=UNKNOWN mode=AT_MOST_ONCE → reconcile required; ");
                }
                case NON_REPLAYABLE -> {
                    requiresWait = true;
                    reason.append("outcome=UNKNOWN mode=NON_REPLAYABLE → operator confirmation required; ");
                }
            }
        }

        if (requiresCompensate) {
            return RecoveryDecision.compensate("Crash with unresolvable in-flight actions: " + reason);
        }
        if (requiresWait) {
            return RecoveryDecision.waitAction("Crash with non-replayable in-flight actions requiring reconciliation: " + reason);
        }

        // All in-flight were safe to replay
        return RecoveryDecision.retry("Crash with replayable in-flight actions (idempotent/at-least-once): " + reason);
    }
}
