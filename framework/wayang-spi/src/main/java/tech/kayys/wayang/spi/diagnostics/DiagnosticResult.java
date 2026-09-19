package tech.kayys.wayang.spi.diagnostics;

import java.time.Instant;
import java.util.List;
import java.util.Map;

public record DiagnosticResult(
        DiagnosticComponent component,
        DiagnosticStatus status,
        Instant checkedAt,
        String summary,
        List<DiagnosticIssue> issues,
        Map<String, Object> attributes) {

    public DiagnosticResult {
        issues = issues == null
                ? List.of()
                : List.copyOf(issues);

        attributes = attributes == null
                ? Map.of()
                : Map.copyOf(attributes);
    }

    public static DiagnosticResult healthy(DiagnosticComponent component, String summary) {
        return new DiagnosticResult(component, DiagnosticStatus.HEALTHY, Instant.now(), summary, List.of(), Map.of());
    }

    public static DiagnosticResult degraded(DiagnosticComponent component, String summary, List<DiagnosticIssue> issues) {
        return new DiagnosticResult(component, DiagnosticStatus.DEGRADED, Instant.now(), summary, issues, Map.of());
    }

    public static DiagnosticResult unhealthy(DiagnosticComponent component, String summary, List<DiagnosticIssue> issues) {
        return new DiagnosticResult(component, DiagnosticStatus.UNHEALTHY, Instant.now(), summary, issues, Map.of());
    }

    public static DiagnosticResult unknown(DiagnosticComponent component, String summary, List<DiagnosticIssue> issues) {
        return new DiagnosticResult(component, DiagnosticStatus.UNKNOWN, Instant.now(), summary, issues, Map.of());
    }
}
