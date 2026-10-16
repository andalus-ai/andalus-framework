package tech.kayys.andalus.harness.planning;

import java.util.Map;
import java.util.Optional;

/**
 * Result of a planning operation (create or revise).
 *
 * <p>Captures the outcome status, the produced plan (if any), a human-readable reason,
 * and optional diagnostic metadata for observability.</p>
 */
public record PlanningResult(
        PlanningStatus status,
        Optional<AgentPlan> plan,
        String reason,
        Map<String, Object> diagnostics) {

    public PlanningResult {
        if (status == null) throw new IllegalArgumentException("status must not be null");
        if (plan == null) throw new IllegalArgumentException("plan must not be null");
        if (reason == null) reason = "";
        if (diagnostics == null) diagnostics = Map.of();
        diagnostics = Map.copyOf(diagnostics);
    }

    // ── factories ──────────────────────────────────────────────────────────────

    /** Successful result with a produced plan. */
    public static PlanningResult success(AgentPlan plan) {
        return new PlanningResult(PlanningStatus.SUCCEEDED, Optional.of(plan), "Plan created successfully.", Map.of());
    }

    /** Successful result with a produced plan and extra diagnostics. */
    public static PlanningResult success(AgentPlan plan, Map<String, Object> diagnostics) {
        return new PlanningResult(PlanningStatus.SUCCEEDED, Optional.of(plan), "Plan created successfully.", diagnostics);
    }

    /** Failure due to validation errors. */
    public static PlanningResult invalid(String reason) {
        return new PlanningResult(PlanningStatus.INVALID, Optional.empty(), reason, Map.of());
    }

    /** Failure due to an unexpected error during planning. */
    public static PlanningResult failed(String reason) {
        return new PlanningResult(PlanningStatus.FAILED, Optional.empty(), reason, Map.of());
    }

    /** Failure with diagnostics. */
    public static PlanningResult failed(String reason, Map<String, Object> diagnostics) {
        return new PlanningResult(PlanningStatus.FAILED, Optional.empty(), reason, diagnostics);
    }

    // ── convenience ───────────────────────────────────────────────────────────

    public boolean isSuccess() {
        return status == PlanningStatus.SUCCEEDED;
    }

    public boolean isFailure() {
        return status == PlanningStatus.FAILED || status == PlanningStatus.INVALID;
    }
}
