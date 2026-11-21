package tech.kayys.andalus.harness.planning;

import java.util.List;

/** Immutable result of validating a stored plan revision chain. */
public record PlanRevisionHistoryIntegrityReport(List<String> errors) {

    public PlanRevisionHistoryIntegrityReport {
        errors = errors == null ? List.of() : List.copyOf(errors);
    }

    public boolean valid() {
        return errors.isEmpty();
    }
}
