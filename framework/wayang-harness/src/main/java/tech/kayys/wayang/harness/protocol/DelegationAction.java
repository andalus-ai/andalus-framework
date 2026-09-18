package tech.kayys.wayang.harness.protocol;

import java.util.Objects;

/**
 * Represents a delegation action.
 *
 * <p>Its components capture `target`, `task`.</p>
 *
 * @param target the target
 * @param task the task
 */


public record DelegationAction(
        AgentRef target,
        AgentTask task
) implements AgentAction {
    public DelegationAction {
        Objects.requireNonNull(target, "target cannot be null");
        Objects.requireNonNull(task, "task cannot be null");
    }
}
