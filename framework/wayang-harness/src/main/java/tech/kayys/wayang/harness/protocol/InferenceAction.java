package tech.kayys.wayang.harness.protocol;

import tech.kayys.wayang.harness.model.ModelIntent;

import java.util.Objects;

/**
 * Represents a inference action.
 *
 * <p>Its components capture `intent`.</p>
 *
 * @param intent the intent
 */


public record InferenceAction(ModelIntent intent) implements AgentAction {
    public InferenceAction {
        Objects.requireNonNull(intent, "ModelIntent cannot be null");
    }
}
