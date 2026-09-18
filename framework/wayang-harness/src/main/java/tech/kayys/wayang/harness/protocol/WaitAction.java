package tech.kayys.wayang.harness.protocol;

import java.util.Objects;

public record WaitAction(WaitCondition condition) implements AgentAction {
    public WaitAction {
        Objects.requireNonNull(condition, "WaitCondition cannot be null");
    }
}
