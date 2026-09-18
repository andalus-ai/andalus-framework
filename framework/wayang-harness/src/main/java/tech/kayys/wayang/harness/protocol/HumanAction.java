package tech.kayys.wayang.harness.protocol;

import java.util.Objects;

/**
 * Represents a human action.
 *
 * <p>Its components capture `request`.</p>
 *
 * @param request the request
 */


public record HumanAction(HumanRequest request) implements AgentAction {
    public HumanAction {
        Objects.requireNonNull(request, "HumanRequest cannot be null");
    }
}
