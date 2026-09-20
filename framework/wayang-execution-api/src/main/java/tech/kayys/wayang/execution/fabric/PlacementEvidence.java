package tech.kayys.wayang.execution.fabric;

import java.util.Map;
import java.util.Objects;

/**
 * Objective evidence and rationale explaining why a specific node was chosen for placement.
 */
public record PlacementEvidence(
    NodeIdentity selectedNode,
    Map<String, Boolean> requirementsMatched,
    double score,
    String rationale
) {
    public PlacementEvidence {
        Objects.requireNonNull(selectedNode, "selectedNode cannot be null");
        requirementsMatched = requirementsMatched != null ? Map.copyOf(requirementsMatched) : Map.of();
        Objects.requireNonNull(rationale, "rationale cannot be null");
    }
}
