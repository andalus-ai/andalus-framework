package tech.kayys.wayang.spi.operator.sandbox;

import java.time.Instant;
import java.util.List;
import java.util.Map;

public record SandboxDiagnosticsSummary(
        String sandboxId,
        Instant timestamp,
        String state,
        String health,
        List<String> warnings,
        Map<String, Object> attributes
) {

    public SandboxDiagnosticsSummary {
        warnings = warnings == null
                ? List.of()
                : List.copyOf(warnings);

        attributes = attributes == null
                ? Map.of()
                : Map.copyOf(attributes);
    }
}
