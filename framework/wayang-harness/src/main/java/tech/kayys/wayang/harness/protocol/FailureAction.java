package tech.kayys.wayang.harness.protocol;

import java.util.Objects;

public record FailureAction(String reason) implements AgentAction {
    public FailureAction {
        Objects.requireNonNull(reason, "reason cannot be null");
    }
}
