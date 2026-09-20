package tech.kayys.wayang.execution.portability;

import java.util.Objects;

/**
 * Pre-import assessment evaluating safety and compatibility of loading an execution package.
 */
public record ImportAssessment(
    boolean acceptable,
    CompatibilityReport compatibility,
    String targetRuntime
) {
    public ImportAssessment {
        Objects.requireNonNull(compatibility, "compatibility cannot be null");
        Objects.requireNonNull(targetRuntime, "targetRuntime cannot be null");
    }
}
