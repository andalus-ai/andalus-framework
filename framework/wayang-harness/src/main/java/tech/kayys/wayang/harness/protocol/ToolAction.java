package tech.kayys.wayang.harness.protocol;

import tech.kayys.wayang.harness.tool.ToolIntent;

import java.util.Objects;

public record ToolAction(ToolIntent intent) implements AgentAction {
    public ToolAction {
        Objects.requireNonNull(intent, "ToolIntent cannot be null");
    }
}
