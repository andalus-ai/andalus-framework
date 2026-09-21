package tech.kayys.andalus.execution.conformance;

import java.util.Objects;

/**
 * Concrete finding detailing a violation or deviation discovered during testing.
 */
public record ConformanceFinding(
    FindingType type,
    String description,
    Severity severity,
    EvidenceReference evidence
) {
    public ConformanceFinding {
        Objects.requireNonNull(type, "type cannot be null");
        Objects.requireNonNull(description, "description cannot be null");
        Objects.requireNonNull(severity, "severity cannot be null");
    }

    public static ConformanceFinding violation(String description, Severity severity, EvidenceReference evidence) {
        return new ConformanceFinding(FindingType.CONTRACT_VIOLATION, description, severity, evidence);
    }

    public static ConformanceFinding defect(String description, Severity severity) {
        return new ConformanceFinding(FindingType.IMPLEMENTATION_DEFECT, description, severity, null);
    }
}
