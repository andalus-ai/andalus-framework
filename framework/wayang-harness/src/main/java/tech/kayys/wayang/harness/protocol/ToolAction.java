package tech.kayys.wayang.harness.protocol;

import tech.kayys.wayang.tool.resolution.ToolIntent;

import java.util.Objects;

/**
 * Represents a tool action.
 *
 * <p>Its components capture `intent`.</p>
 *
 * @param intent the intent
 */


public record ToolAction(ToolIntent intent) implements AgentAction {
    public ToolAction {
        Objects.requireNonNull(intent, "ToolIntent cannot be null");
    }
}
