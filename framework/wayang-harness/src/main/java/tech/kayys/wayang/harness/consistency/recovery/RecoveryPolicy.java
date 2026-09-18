package tech.kayys.wayang.harness.consistency.recovery;

import tech.kayys.wayang.harness.consistency.state.ExecutionRecord;
import tech.kayys.wayang.harness.consistency.state.FailureInfo;

/**
 * Strategy interface for selecting recovery decisions based on structured failure info.
 */
public interface RecoveryPolicy {

    RecoveryDecision decide(FailureInfo failure, ExecutionRecord execution);
}
