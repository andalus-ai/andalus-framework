package tech.kayys.wayang.state.reproducibility;

import java.util.Map;
import java.util.Objects;

public record ReplayDescriptor(
        String originalExecutionId,
        ExecutionFingerprint fingerprint,
        ReproducibilityLevel level,
        Map<String, Object> parameters
) {
    public ReplayDescriptor {
        Objects.requireNonNull(originalExecutionId, "originalExecutionId cannot be null");
        Objects.requireNonNull(fingerprint, "fingerprint cannot be null");
        Objects.requireNonNull(level, "level cannot be null");
        parameters = parameters == null ? Map.of() : Map.copyOf(parameters);
    }
}
