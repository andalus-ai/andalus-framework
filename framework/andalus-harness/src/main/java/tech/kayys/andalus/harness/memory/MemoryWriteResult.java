package tech.kayys.andalus.harness.memory;

import java.util.Map;
import java.util.Optional;

/**
 * Structured result of an attempt to admit/write a memory entry into the runtime.
 */
public record MemoryWriteResult(
        WriteStatus status,
        Optional<MemoryId> memoryId,
        String reason,
        Map<String, Object> diagnostics
) {

    public MemoryWriteResult {
        memoryId = memoryId == null ? Optional.empty() : memoryId;
        reason = reason == null ? "" : reason;
        diagnostics = diagnostics == null ? Map.of() : Map.copyOf(diagnostics);
    }

    public static MemoryWriteResult admitted(MemoryId id) {
        return new MemoryWriteResult(WriteStatus.ADMITTED, Optional.of(id), "Memory admitted", Map.of());
    }

    public static MemoryWriteResult rejected(String reason) {
        return new MemoryWriteResult(WriteStatus.REJECTED, Optional.empty(), reason, Map.of());
    }

    public static MemoryWriteResult conflict(String reason) {
        return new MemoryWriteResult(WriteStatus.CONFLICT, Optional.empty(), reason, Map.of());
    }

    public static MemoryWriteResult policyDenied(String reason) {
        return new MemoryWriteResult(WriteStatus.POLICY_DENIED, Optional.empty(), reason, Map.of());
    }
}
