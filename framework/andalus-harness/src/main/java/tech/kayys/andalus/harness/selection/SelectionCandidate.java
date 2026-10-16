package tech.kayys.andalus.harness.selection;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Represents a proposed tool or skill candidate that satisfies the selection criteria.
 */
public record SelectionCandidate(
        String candidateId,
        SelectionTarget target,
        List<String> matchedCapabilities,
        List<String> unmetOptionalRequirements,
        String rationale,
        Map<String, Object> metadata
) {
    public SelectionCandidate {
        if (candidateId == null || candidateId.isBlank()) {
            throw new IllegalArgumentException("candidateId is required");
        }
        Objects.requireNonNull(target, "target is required");

        matchedCapabilities = matchedCapabilities == null
                ? List.of() : List.copyOf(matchedCapabilities);
        unmetOptionalRequirements = unmetOptionalRequirements == null
                ? List.of() : List.copyOf(unmetOptionalRequirements);
        rationale = rationale == null ? "" : rationale;
        metadata = metadata == null ? Map.of() : Map.copyOf(metadata);
    }

    public static SelectionCandidate tool(String candidateId, List<String> matchedCapabilities, String rationale) {
        return new SelectionCandidate(candidateId, SelectionTarget.TOOL, matchedCapabilities, List.of(), rationale, Map.of());
    }

    public static SelectionCandidate skill(String candidateId, List<String> matchedCapabilities, String rationale) {
        return new SelectionCandidate(candidateId, SelectionTarget.SKILL, matchedCapabilities, List.of(), rationale, Map.of());
    }
}
