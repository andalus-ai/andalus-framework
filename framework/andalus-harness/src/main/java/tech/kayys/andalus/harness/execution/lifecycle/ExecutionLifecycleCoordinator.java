package tech.kayys.andalus.harness.execution.lifecycle;

import tech.kayys.andalus.harness.execution.state.ExecutionState;
import tech.kayys.andalus.harness.lifecycle.HarnessExecutionStatus;

/**
 * Coordinates the in-process lifecycle projection with the durable execution state (6.15.3.2).
 *
 * <p>The <strong>durable state store is authoritative</strong>. The {@code HarnessLifecycle}
 * is the process-local control surface and projection used by running code.
 *
 * <p>This intentionally thin interface ensures:
 * <ul>
 *   <li>Durable state transitions happen before in-memory lifecycle transitions</li>
 *   <li>A process crash between the two leaves a recoverable durable record</li>
 *   <li>No caller needs to know the mapping between the two status vocabularies</li>
 * </ul>
 */
public interface ExecutionLifecycleCoordinator {

    /** Returns the current durable execution state (authoritative). */
    ExecutionState state();

    /** Returns the current in-process lifecycle status (projection). */
    HarnessExecutionStatus lifecycleStatus();

    /**
     * Transitions the execution to the target lifecycle status.
     *
     * <p>Sequence:
     * <ol>
     *   <li>Map {@code target} to the corresponding durable {@link tech.kayys.andalus.harness.execution.state.ExecutionStatus}</li>
     *   <li>Persist the new durable state (optimistic concurrency)</li>
     *   <li>Transition the in-process lifecycle</li>
     * </ol>
     *
     * <p>If the process crashes between steps 2 and 3, the durable state is already committed
     * and recovery can reconstruct the lifecycle projection.
     *
     * @param target the desired lifecycle status
     * @throws IllegalStateException if the transition is not permitted from the current state
     */
    void transitionTo(HarnessExecutionStatus target);

    /** Returns {@code true} if the execution is in a terminal durable state. */
    boolean isTerminal();
}
