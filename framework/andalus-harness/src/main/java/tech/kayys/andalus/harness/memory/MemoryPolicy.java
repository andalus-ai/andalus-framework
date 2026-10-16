package tech.kayys.andalus.harness.memory;

import java.time.Duration;
import java.util.Map;

/**
 * Rules defining retention, sensitivity, and operational constraints for memory.
 */
public record MemoryPolicy(
        MemoryScope scope,
        Duration retention,
        double initialImportance,
        boolean sensitive,
        boolean allowAdmission,
        boolean allowRetrieval,
        boolean allowDeletion,
        Map<String, Object> attributes
) {

    public MemoryPolicy {
        scope = scope == null ? MemoryScope.AGENT : scope;
        attributes = attributes == null ? Map.of() : Map.copyOf(attributes);
    }

    public static MemoryPolicy ephemeral(MemoryScope scope) {
        return new MemoryPolicy(
                scope,
                Duration.ofHours(1),
                0.1,
                false,
                true,
                true,
                true,
                Map.of()
        );
    }

    public static MemoryPolicy permanent(MemoryScope scope) {
        return new MemoryPolicy(
                scope,
                null,
                1.0,
                false,
                true,
                true,
                true,
                Map.of()
        );
    }
}
