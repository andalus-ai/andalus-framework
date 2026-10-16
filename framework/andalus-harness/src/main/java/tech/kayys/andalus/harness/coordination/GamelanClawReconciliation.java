package tech.kayys.andalus.harness.coordination;

import tech.kayys.andalus.harness.execution.state.ExecutionStatus;

/**
 * Outcome of reconciling one Gamelan dispatch during startup (6.17.3.11).
 *
 * @param dispatch         the Gamelan dispatch that was inspected
 * @param binding          the Claw binding found, if any
 * @param action           the synchronization action required
 * @param executionStatus  the observed Claw execution status, if the execution exists
 */
public record GamelanClawReconciliation(
        GamelanDispatchDescriptor dispatch,
        java.util.Optional<AgentExecutionBinding> binding,
        GamelanReconciliationAction action,
        java.util.Optional<ExecutionStatus> executionStatus
) {
    public GamelanClawReconciliation {
        java.util.Objects.requireNonNull(dispatch, "dispatch");
        java.util.Objects.requireNonNull(binding, "binding");
        java.util.Objects.requireNonNull(action, "action");
        java.util.Objects.requireNonNull(executionStatus, "executionStatus");
    }

    /** @return true when Gamelan must be told about a terminal Claw outcome. */
    public boolean requiresGamelanNotification() {
        return action == GamelanReconciliationAction.NOTIFY_COMPLETED
                || action == GamelanReconciliationAction.NOTIFY_FAILED
                || action == GamelanReconciliationAction.NOTIFY_CANCELLED
                || action == GamelanReconciliationAction.MARK_LOST;
    }
}
