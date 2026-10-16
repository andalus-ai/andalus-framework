package tech.kayys.andalus.harness.inference.planning;

import tech.kayys.andalus.harness.inference.InferencePlan;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Result of an inference planning operation.
 */
public record InferencePlanningResult(
        InferencePlanningStatus status,
        Optional<InferencePlan> plan,
        List<InferenceCandidateExclusion> exclusions,
        Map<String, Object> diagnostics
) {
    public InferencePlanningResult {
        Objects.requireNonNull(status, "status is required");
        plan = plan == null ? Optional.empty() : plan;
        exclusions = exclusions == null ? List.of() : List.copyOf(exclusions);
        diagnostics = diagnostics == null ? Map.of() : Map.copyOf(diagnostics);
    }

    public static InferencePlanningResult planned(InferencePlan plan, List<InferenceCandidateExclusion> exclusions) {
        return new InferencePlanningResult(InferencePlanningStatus.PLANNED, Optional.of(plan), exclusions, Map.of());
    }

    public static InferencePlanningResult planned(InferencePlan plan, List<InferenceCandidateExclusion> exclusions, Map<String, Object> diagnostics) {
        return new InferencePlanningResult(InferencePlanningStatus.PLANNED, Optional.of(plan), exclusions, diagnostics);
    }

    public static InferencePlanningResult noFeasibleCandidate(List<InferenceCandidateExclusion> exclusions) {
        return new InferencePlanningResult(InferencePlanningStatus.NO_FEASIBLE_CANDIDATE, Optional.empty(), exclusions, Map.of());
    }

    public static InferencePlanningResult failed(InferencePlanningStatus status, String reason, List<InferenceCandidateExclusion> exclusions) {
        return new InferencePlanningResult(status, Optional.empty(), exclusions, Map.of("reason", reason != null ? reason : ""));
    }

    public boolean isSuccess() {
        return status == InferencePlanningStatus.PLANNED && plan.isPresent();
    }
}
