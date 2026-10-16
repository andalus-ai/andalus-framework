package tech.kayys.andalus.harness.execution.state;

import java.util.Collection;

/**
 * Optional capability for discovering executions during startup and recovery (6.15.6.14).
 *
 * <p>Separated from {@link ExecutionStateStore} to keep the core CRUD contract minimal
 * while allowing startup scanners and recovery workers to discover dormant executions.</p>
 */
public interface ExecutionStateDiscovery {

    /**
     * Finds the IDs of all executions currently in {@link ExecutionStatus#WAITING} state.
     *
     * @return collection of waiting execution IDs
     */
    Collection<ExecutionId> findWaitingExecutions();
}
