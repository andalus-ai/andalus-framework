package tech.kayys.wayang.harness.protocol;

import java.util.Objects;

/**
 * Represents a wait action.
 *
 * <p>Its components capture `condition`.</p>
 *
 * @param condition the condition
 */


public record WaitAction(WaitCondition condition) implements AgentAction {
    public WaitAction {
        Objects.requireNonNull(condition, "WaitCondition cannot be null");
    }
}
