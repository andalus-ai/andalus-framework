package tech.kayys.andalus.execution.portability;

import tech.kayys.andalus.execution.conformance.CapabilityId;
import java.util.List;
import java.util.Set;

/**
 * Report evaluating whether a portable execution is compatible with a target runtime.
 */
public record CompatibilityReport(
    boolean compatible,
    List<CompatibilityIssue> issues,
    Set<CapabilityId> supportedCapabilities,
    Set<CapabilityId> missingCapabilities
) {
    public CompatibilityReport {
        issues = issues != null ? List.copyOf(issues) : List.of();
        supportedCapabilities = supportedCapabilities != null ? Set.copyOf(supportedCapabilities) : Set.of();
        missingCapabilities = missingCapabilities != null ? Set.copyOf(missingCapabilities) : Set.of();
    }

    public static CompatibilityReport success(Set<CapabilityId> supported) {
        return new CompatibilityReport(true, List.of(), supported, Set.of());
    }

    public static CompatibilityReport failure(List<CompatibilityIssue> issues, Set<CapabilityId> missing) {
        return new CompatibilityReport(false, issues, Set.of(), missing);
    }
}
