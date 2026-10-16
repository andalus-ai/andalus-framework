package tech.kayys.andalus.context.assembly;

import java.util.Map;

/**
 * Diagnostics and metric outcomes produced during a context assembly execution.
 */
public record ContextAssemblyDiagnostics(
        int candidatesCount,
        int selectedCount,
        long totalEstimatedTokens,
        long durationMs,
        Map<String, Object> metadata
) {

    public ContextAssemblyDiagnostics {
        metadata = metadata == null ? Map.of() : Map.copyOf(metadata);
    }

    public static ContextAssemblyDiagnostics of(int candidates, int selected, long tokens, long durationMs) {
        return new ContextAssemblyDiagnostics(candidates, selected, tokens, durationMs, Map.of());
    }
}
