package tech.kayys.andalus.execution.fabric;

import tech.kayys.andalus.execution.mobility.ExecutionIdentity;

import java.time.Instant;
import java.util.Objects;

/**
 * Deterministic placement decision binding an execution to an optimal target node.
 */
public record PlacementPlan(
    ExecutionIdentity executionId,
    NodeIdentity targetNode,
    PlacementEvidence evidence,
    Instant plannedAt
) {
    public PlacementPlan {
        Objects.requireNonNull(executionId, "executionId cannot be null");
        Objects.requireNonNull(targetNode, "targetNode cannot be null");
        Objects.requireNonNull(evidence, "evidence cannot be null");
        Objects.requireNonNull(plannedAt, "plannedAt cannot be null");
    }

    public static PlacementPlan of(ExecutionIdentity executionId, NodeIdentity targetNode, PlacementEvidence evidence) {
        return new PlacementPlan(executionId, targetNode, evidence, Instant.now());
    }
}
