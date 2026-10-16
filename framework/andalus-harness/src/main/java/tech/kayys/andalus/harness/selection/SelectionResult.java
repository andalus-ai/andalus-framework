package tech.kayys.andalus.harness.selection;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Result of evaluating available tool and skill candidates against a selection request.
 */
public record SelectionResult(
        SelectionStatus status,
        List<SelectionCandidate> candidates,
        List<CandidateExclusion> exclusions,
        Map<String, Object> diagnostics
) {
    public SelectionResult {
        Objects.requireNonNull(status, "status is required");
        candidates = candidates == null ? List.of() : List.copyOf(candidates);
        exclusions = exclusions == null ? List.of() : List.copyOf(exclusions);
        diagnostics = diagnostics == null ? Map.of() : Map.copyOf(diagnostics);
    }

    public static SelectionResult found(List<SelectionCandidate> candidates, List<CandidateExclusion> exclusions) {
        return new SelectionResult(SelectionStatus.CANDIDATES_FOUND, candidates, exclusions, Map.of());
    }

    public static SelectionResult noCandidates(List<CandidateExclusion> exclusions) {
        return new SelectionResult(SelectionStatus.NO_CANDIDATES, List.of(), exclusions, Map.of());
    }

    public static SelectionResult capabilityUnavailable(String capability) {
        return new SelectionResult(
                SelectionStatus.CAPABILITY_UNAVAILABLE,
                List.of(),
                List.of(),
                Map.of("missingCapability", capability != null ? capability : "")
        );
    }

    public static SelectionResult failed(String reason) {
        return new SelectionResult(
                SelectionStatus.FAILED,
                List.of(),
                List.of(),
                Map.of("reason", reason != null ? reason : "")
        );
    }

    public boolean hasCandidates() {
        return status == SelectionStatus.CANDIDATES_FOUND && !candidates.isEmpty();
    }
}
