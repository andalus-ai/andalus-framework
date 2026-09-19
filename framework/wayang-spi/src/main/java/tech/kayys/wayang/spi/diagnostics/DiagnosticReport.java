package tech.kayys.wayang.spi.diagnostics;

import java.time.Instant;
import java.util.List;
import java.util.Map;

public record DiagnosticReport(
        Instant checkedAt,
        DiagnosticStatus overallStatus,
        List<DiagnosticResult> results,
        Map<String, Object> attributes) {

    public DiagnosticReport {
        results = results == null
                ? List.of()
                : List.copyOf(results);

        attributes = attributes == null
                ? Map.of()
                : Map.copyOf(attributes);
    }
}
