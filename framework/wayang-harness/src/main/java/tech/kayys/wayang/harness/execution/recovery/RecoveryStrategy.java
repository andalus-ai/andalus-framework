package tech.kayys.wayang.harness.execution.recovery;

import tech.kayys.wayang.harness.execution.state.ExecutionState;

/**
 * Defines the contract for recovery strategy operations in the Wayang framework.
 */


public interface RecoveryStrategy {

    RecoveryDecision recover(ExecutionState state, RecoveryContext context);
}
