package tech.kayys.andalus.memory.session;

import java.util.Objects;
import java.util.UUID;

/**
 * Lightweight handle referencing stored memory in a specific tier without bloating context.
 */
public record MemoryReference(
        String referenceId,
        MemoryTier tier,
        String label) {

    public MemoryReference {
        Objects.requireNonNull(referenceId, "referenceId must not be null");
        Objects.requireNonNull(tier, "tier must not be null");
        label = label != null ? label : "";
    }

    public static MemoryReference generate(MemoryTier tier, String label) {
        return new MemoryReference("mem-ref-" + UUID.randomUUID(), tier, label);
    }
}
