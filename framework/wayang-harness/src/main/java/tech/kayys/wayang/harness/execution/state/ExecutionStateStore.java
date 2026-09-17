package tech.kayys.wayang.harness.execution.state;

import java.util.Optional;

/**
 * Storage contract supporting optimistic concurrency version checks.
 */
public interface ExecutionStateStore {

    Optional<ExecutionState> load(ExecutionId executionId);

    void save(ExecutionState state, long expectedVersion);
}
