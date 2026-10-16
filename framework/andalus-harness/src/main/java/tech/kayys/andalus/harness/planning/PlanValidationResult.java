package tech.kayys.andalus.harness.planning;

import java.util.List;

/**
 * Result of validating an {@link AgentPlan}.
 */
public record PlanValidationResult(
        boolean valid,
        List<String> errors,
        List<String> warnings) {

    public PlanValidationResult {
        if (errors == null) errors = List.of();
        if (warnings == null) warnings = List.of();
        errors = List.copyOf(errors);
        warnings = List.copyOf(warnings);
    }

    /** Validation passed with no errors or warnings. */
    public static PlanValidationResult accepted() {
        return new PlanValidationResult(true, List.of(), List.of());
    }

    /** Validation passed but with warnings. */
    public static PlanValidationResult acceptedWithWarnings(List<String> warnings) {
        return new PlanValidationResult(true, List.of(), warnings);
    }

    /** Validation failed with one or more errors. */
    public static PlanValidationResult rejected(List<String> errors) {
        return new PlanValidationResult(false, errors, List.of());
    }

    /** Validation failed with errors and warnings. */
    public static PlanValidationResult rejected(List<String> errors, List<String> warnings) {
        return new PlanValidationResult(false, errors, warnings);
    }

    /** Validation failed with a single error message. */
    public static PlanValidationResult rejected(String error) {
        return new PlanValidationResult(false, List.of(error), List.of());
    }
}
