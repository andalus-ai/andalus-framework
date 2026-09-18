package tech.kayys.wayang.harness.protocol;

import java.util.Objects;

public record DelegationAction(
        AgentRef target,
        AgentTask task
) implements AgentAction {
    public DelegationAction {
        Objects.requireNonNull(target, "target cannot be null");
        Objects.requireNonNull(task, "task cannot be null");
    }
}
