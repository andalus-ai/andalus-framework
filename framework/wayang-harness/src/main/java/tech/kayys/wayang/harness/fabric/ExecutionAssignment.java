package tech.kayys.wayang.harness.fabric;

import tech.kayys.wayang.harness.consistency.state.ExecutionId;
import tech.kayys.wayang.harness.workflow.NodeId;

/**
 * Contract representing an assigned task delegated to a worker with fencing.
 */
public interface ExecutionAssignment {

    AssignmentId id();

    ExecutionId executionId();

    NodeId nodeId();

    AttemptId attemptId();

    WorkerId workerId();

    AssignmentFence fence();

    AssignmentStatus status();

    AssignmentConstraints constraints();
}
