package tech.kayys.wayang.execution.fabric;

import tech.kayys.wayang.execution.mobility.ExecutionIdentity;
import tech.kayys.wayang.execution.mobility.ExecutionLease;

import java.time.Instant;
import java.util.Objects;

/**
 * Active binding associating a running execution with its assigned node and lease.
 */
public record ExecutionPlacement(
    ExecutionIdentity executionId,
    NodeIdentity currentNode,
    ExecutionLease lease,
    Instant placedAt
) {
    public ExecutionPlacement {
        Objects.requireNonNull(executionId, "executionId cannot be null");
        Objects.requireNonNull(currentNode, "currentNode cannot be null");
        Objects.requireNonNull(lease, "lease cannot be null");
        Objects.requireNonNull(placedAt, "placedAt cannot be null");
    }
}
