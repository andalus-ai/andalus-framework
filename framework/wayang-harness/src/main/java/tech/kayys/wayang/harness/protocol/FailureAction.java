package tech.kayys.wayang.harness.protocol;

import java.util.Objects;

/**
 * Represents a failure action.
 *
 * <p>Its components capture `reason`.</p>
 *
 * @param reason the reason
 */


public record FailureAction(String reason) implements AgentAction {
    public FailureAction {
        Objects.requireNonNull(reason, "reason cannot be null");
    }
}
