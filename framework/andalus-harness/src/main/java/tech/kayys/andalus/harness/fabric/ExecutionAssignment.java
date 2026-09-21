package tech.kayys.andalus.harness.fabric;

import tech.kayys.andalus.harness.consistency.state.ExecutionId;
import tech.kayys.andalus.workflow.graph.NodeId;

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
