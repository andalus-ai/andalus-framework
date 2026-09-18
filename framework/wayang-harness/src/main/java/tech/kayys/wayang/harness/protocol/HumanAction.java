package tech.kayys.wayang.harness.protocol;

import java.util.Objects;

public record HumanAction(HumanRequest request) implements AgentAction {
    public HumanAction {
        Objects.requireNonNull(request, "HumanRequest cannot be null");
    }
}
