package tech.kayys.andalus.skill.spi;
import java.time.Duration;
import java.util.Map;

public record SkillRuntimeConstraints(
        Duration timeout,
        long maxMemoryBytes,
        long maxCpuMillis,
        long maxOutputBytes,
        Map<String, Object> attributes
) {
    public SkillRuntimeConstraints {
        timeout = timeout == null ? Duration.ZERO : timeout;
        attributes = attributes == null ? Map.of() : Map.copyOf(attributes);
        if (timeout.isNegative()) throw new IllegalArgumentException("timeout must not be negative");
        if (maxMemoryBytes < 0) throw new IllegalArgumentException("maxMemoryBytes must not be negative");
        if (maxCpuMillis < 0) throw new IllegalArgumentException("maxCpuMillis must not be negative");
        if (maxOutputBytes < 0) throw new IllegalArgumentException("maxOutputBytes must not be negative");
    }
    public static SkillRuntimeConstraints unlimited() {
        return new SkillRuntimeConstraints(Duration.ZERO, 0, 0, 0, Map.of());
    }
}
