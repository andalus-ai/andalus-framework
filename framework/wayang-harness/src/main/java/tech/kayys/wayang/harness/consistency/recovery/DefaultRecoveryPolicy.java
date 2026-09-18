package tech.kayys.wayang.harness.consistency.recovery;

import tech.kayys.wayang.harness.consistency.state.ExecutionRecord;
import tech.kayys.wayang.harness.consistency.state.FailureClass;
import tech.kayys.wayang.harness.consistency.state.FailureInfo;

/**
 * Standard implementation of {@link RecoveryPolicy} mapping failure semantics to recovery decisions.
 */
public class DefaultRecoveryPolicy implements RecoveryPolicy {

    @Override
    public RecoveryDecision decide(FailureInfo failure, ExecutionRecord execution) {
        if (failure == null) {
            return RecoveryDecision.FAIL;
        }

        if (failure.classification() == FailureClass.TRANSIENT && failure.retryable()) {
            return RecoveryDecision.RETRY;
        }

        if (execution != null && execution.checkpoint().isPresent()) {
            return RecoveryDecision.RESUME;
        }

        if (failure.compensatable()) {
            return RecoveryDecision.COMPENSATE;
        }

        return RecoveryDecision.FAIL;
    }
}
