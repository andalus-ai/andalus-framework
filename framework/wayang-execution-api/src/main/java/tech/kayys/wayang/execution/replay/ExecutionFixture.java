package tech.kayys.wayang.execution.replay;

import java.util.Map;
import java.util.Objects;

/**
 * Test fixture defining golden execution and overrides for the ExecutionTestHarness.
 */
public record ExecutionFixture(
        String name,
        ReplayManifest manifest,
        Map<String, Object> initialOverrides
) {
    public ExecutionFixture {
        Objects.requireNonNull(name, "name cannot be null");
        Objects.requireNonNull(manifest, "manifest cannot be null");
        initialOverrides = initialOverrides == null ? Map.of() : Map.copyOf(initialOverrides);
    }
}
