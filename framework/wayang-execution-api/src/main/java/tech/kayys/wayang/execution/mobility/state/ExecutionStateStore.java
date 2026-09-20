package tech.kayys.wayang.execution.mobility.state;

import tech.kayys.wayang.execution.mobility.ExecutionDurableState;
import tech.kayys.wayang.execution.mobility.FencingToken;

import java.util.Optional;

/**
 * Authoritative durable source of execution state in the Wayang State Plane.
 */
public interface ExecutionStateStore {

    /**
     * Load current durable state for an execution.
     */
    Optional<ExecutionDurableState> load(String executionId);

    /**
     * Save updated state guarded by a fencing token and optimistic sequence checks.
     */
    void save(ExecutionDurableState state, FencingToken fencingToken);

    /**
     * Get current version sequence of an execution.
     */
    long version(String executionId);
}
