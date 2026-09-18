package tech.kayys.wayang.harness.protocol;

import java.util.Optional;

/**
 * Represents a checkpoint action.
 *
 * <p>Its components capture `label`.</p>
 *
 * @param label the label
 */


public record CheckpointAction(Optional<String> label) implements AgentAction {
    public CheckpointAction {
        if (label == null) {
            label = Optional.empty();
        }
    }

    public static CheckpointAction of(String label) {
        return new CheckpointAction(Optional.ofNullable(label));
    }

    public static CheckpointAction empty() {
        return new CheckpointAction(Optional.empty());
    }
}
