package tech.kayys.wayang.harness.execution.recovery;

import tech.kayys.wayang.harness.execution.state.ExecutionState;

public interface RecoveryStrategy {

    RecoveryDecision recover(ExecutionState state, RecoveryContext context);
}
