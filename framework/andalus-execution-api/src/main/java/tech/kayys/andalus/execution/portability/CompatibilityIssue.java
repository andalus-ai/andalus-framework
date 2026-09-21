package tech.kayys.andalus.execution.portability;

import tech.kayys.andalus.execution.conformance.Severity;
import java.util.Objects;

/**
 * Diagnostic issue identifying an incompatibility during portability evaluation.
 */
public record CompatibilityIssue(String code, String message, Severity severity) {

    public CompatibilityIssue {
        Objects.requireNonNull(code, "code cannot be null");
        Objects.requireNonNull(message, "message cannot be null");
        Objects.requireNonNull(severity, "severity cannot be null");
    }

    public static CompatibilityIssue error(String code, String message) {
        return new CompatibilityIssue(code, message, Severity.ERROR);
    }

    public static CompatibilityIssue warning(String code, String message) {
        return new CompatibilityIssue(code, message, Severity.WARNING);
    }
}
