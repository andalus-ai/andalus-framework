package tech.kayys.wayang.harness.fabric;

import tech.kayys.wayang.harness.consistency.state.ExecutionId;
import tech.kayys.wayang.workflow.graph.NodeId;

import java.util.Objects;

/**
 * Immutable reference record implementing {@link ExecutionAssignment}.
 */
public record DefaultExecutionAssignment(
        AssignmentId id,
        ExecutionId executionId,
        NodeId nodeId,
        AttemptId attemptId,
        WorkerId workerId,
        AssignmentFence fence,
        AssignmentStatus status,
        AssignmentConstraints constraints
) implements ExecutionAssignment {

    public DefaultExecutionAssignment {
        Objects.requireNonNull(id, "AssignmentId cannot be null");
        Objects.requireNonNull(executionId, "ExecutionId cannot be null");
        Objects.requireNonNull(nodeId, "NodeId cannot be null");
        Objects.requireNonNull(attemptId, "AttemptId cannot be null");
        Objects.requireNonNull(workerId, "WorkerId cannot be null");
        Objects.requireNonNull(fence, "AssignmentFence cannot be null");
        status = status != null ? status : AssignmentStatus.PENDING;
        constraints = constraints != null ? constraints : AssignmentConstraints.defaults();
    }

    public static DefaultExecutionAssignment create(
            ExecutionId executionId,
            NodeId nodeId,
            WorkerId workerId,
            long generation,
            AssignmentConstraints constraints
    ) {
        AssignmentId id = AssignmentId.generate();
        return new DefaultExecutionAssignment(
                id,
                executionId,
                nodeId,
                AttemptId.initial(),
                workerId,
                AssignmentFence.of(id, generation),
                AssignmentStatus.PENDING,
                constraints
        );
    }

    public DefaultExecutionAssignment withStatus(AssignmentStatus newStatus) {
        return new DefaultExecutionAssignment(id, executionId, nodeId, attemptId, workerId, fence, newStatus, constraints);
    }
}
