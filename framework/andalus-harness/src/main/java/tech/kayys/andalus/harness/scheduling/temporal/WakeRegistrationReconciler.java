package tech.kayys.andalus.harness.scheduling.temporal;

import tech.kayys.andalus.harness.execution.state.ExecutionId;

import java.util.Collection;

/**
 * Reconciles transient wake registrations for executions in durable WAITING state after a crash or restart (6.15.6.4).
 *
 * <p>The reconciler never transitions an execution into WAITING. It operates only on executions
 * that are already durably WAITING in the state store (6.15.6.5).</p>
 */
public interface WakeRegistrationReconciler {

    /**
     * Reconciles the transient wake registration for a single WAITING execution.
     *
     * @param executionId the execution to reconcile
     */
    void reconcile(ExecutionId executionId);

    /**
     * Reconciles transient wake registrations for a collection of WAITING executions.
     *
     * @param executionIds the executions to reconcile
     */
    default void reconcile(Collection<ExecutionId> executionIds) {
        if (executionIds != null) {
            for (ExecutionId id : executionIds) {
                reconcile(id);
            }
        }
    }
}
