package tech.kayys.wayang.harness.contract.model;

import java.util.Objects;

/**
 * Logical classification or role of an agent (e.g. "flutter-reviewer", "coder", "planner").
 */
public record AgentType(String value) {
    public AgentType {
        Objects.requireNonNull(value, "value cannot be null");
    }

    public static AgentType of(String value) {
        return new AgentType(value);
    }
}
