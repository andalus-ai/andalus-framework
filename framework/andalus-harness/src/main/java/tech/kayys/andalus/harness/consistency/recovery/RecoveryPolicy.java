package tech.kayys.andalus.harness.consistency.recovery;

import tech.kayys.andalus.harness.consistency.state.ExecutionRecord;
import tech.kayys.andalus.harness.consistency.state.FailureInfo;

/**
 * Strategy interface for selecting recovery decisions based on structured failure info.
 */
public interface RecoveryPolicy {

    RecoveryDecision decide(FailureInfo failure, ExecutionRecord execution);
}
