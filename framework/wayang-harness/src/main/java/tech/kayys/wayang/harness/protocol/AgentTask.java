package tech.kayys.wayang.harness.protocol;

import java.util.Map;
import java.util.Objects;

/**
 * Represents a agent task.
 *
 * <p>Its components capture `description`, `input`.</p>
 *
 * @param description the description
 * @param input the input
 */


public record AgentTask(
        String description,
        Map<String, Object> input
) {
    public AgentTask {
        Objects.requireNonNull(description, "description cannot be null");
        input = input != null ? Map.copyOf(input) : Map.of();
    }

    public static AgentTask of(String description) {
        return new AgentTask(description, Map.of());
    }
}
