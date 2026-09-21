package tech.kayys.andalus.execution.conformance;

import java.time.Instant;
import java.util.Objects;
import java.util.Set;

/**
 * Report detailing verified conformance of a runtime or component against claimed contracts.
 */
public record ConformanceReport(
    ProfileId profile,
    Instant executedAt,
    Set<ConformanceResult> results,
    ConformanceSummary summary
) {
    public ConformanceReport {
        Objects.requireNonNull(profile, "profile cannot be null");
        Objects.requireNonNull(executedAt, "executedAt cannot be null");
        results = results != null ? Set.copyOf(results) : Set.of();
        Objects.requireNonNull(summary, "summary cannot be null");
    }

    public boolean isConformant() {
        return summary.failed() == 0;
    }
}
