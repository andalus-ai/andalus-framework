package tech.kayys.wayang.spi.diagnostics;

import java.util.Map;

public record DiagnosticIssue(
        String code,
        DiagnosticSeverity severity,
        String message,
        Map<String, Object> attributes) {

    public DiagnosticIssue {
        if (code == null || code.isBlank()) {
            throw new IllegalArgumentException(
                    "code must not be blank");
        }

        if (severity == null) {
            throw new IllegalArgumentException(
                    "severity must not be null");
        }

        if (message == null || message.isBlank()) {
            throw new IllegalArgumentException(
                    "message must not be blank");
        }

        attributes = attributes == null
                ? Map.of()
                : Map.copyOf(attributes);
    }

    public static DiagnosticIssue info(String code, String message) {
        return new DiagnosticIssue(code, DiagnosticSeverity.INFO, message, Map.of());
    }

    public static DiagnosticIssue warning(String code, String message) {
        return new DiagnosticIssue(code, DiagnosticSeverity.WARNING, message, Map.of());
    }

    public static DiagnosticIssue error(String code, String message) {
        return new DiagnosticIssue(code, DiagnosticSeverity.ERROR, message, Map.of());
    }

    public static DiagnosticIssue critical(String code, String message) {
        return new DiagnosticIssue(code, DiagnosticSeverity.CRITICAL, message, Map.of());
    }
}
