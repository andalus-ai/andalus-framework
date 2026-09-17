package tech.kayys.wayang.harness.execution.recovery;

import tech.kayys.wayang.harness.execution.state.ExecutionState;
import tech.kayys.wayang.harness.execution.state.ExecutionStatus;

import java.util.Objects;

/**
 * Conservative recovery strategy preventing blind replay of external side effects.
 */
public class DefaultRecoveryStrategy implements RecoveryStrategy {

    @Override
    public RecoveryDecision recover(ExecutionState state, RecoveryContext context) {
        Objects.requireNonNull(context, "context");

        if (state == null) {
            return RecoveryDecision.fail("No prior execution state found to recover from");
        }

        if (state.status() == ExecutionStatus.SUSPENDED || state.status() == ExecutionStatus.WAITING) {
            return RecoveryDecision.resume("Execution was safely suspended; ready to resume from cursor " + state.cursor().phaseId());
        }

        if (context.failure().isPresent()) {
            Throwable err = context.failure().get();
            String msg = err.getMessage() != null ? err.getMessage().toLowerCase() : "";
            if (msg.contains("timeout") || msg.contains("connection") || msg.contains("retryable")) {
                return RecoveryDecision.retry("Transient failure detected: " + err.getMessage());
            }
        }

        return RecoveryDecision.waitAction("Side-effect boundary reached or uncertain state: requires manual verification before replay");
    }
}
