package tech.kayys.wayang.harness.protocol;

import java.util.Map;

public record AgentOutput(
        String summary,
        Object result,
        Map<String, Object> metadata
) {
    public AgentOutput {
        metadata = metadata != null ? Map.copyOf(metadata) : Map.of();
    }

    public static AgentOutput of(String summary, Object result) {
        return new AgentOutput(summary, result, Map.of());
    }

    public static AgentOutput success(String summary) {
        return new AgentOutput(summary, null, Map.of());
    }
}
