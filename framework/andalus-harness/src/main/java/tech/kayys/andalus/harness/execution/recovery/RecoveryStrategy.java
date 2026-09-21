package tech.kayys.andalus.harness.execution.recovery;

import tech.kayys.andalus.harness.execution.state.ExecutionState;

/**
 * Defines the contract for recovery strategy operations in the Andalus framework.
 */


public interface RecoveryStrategy {

    RecoveryDecision recover(ExecutionState state, RecoveryContext context);
}
