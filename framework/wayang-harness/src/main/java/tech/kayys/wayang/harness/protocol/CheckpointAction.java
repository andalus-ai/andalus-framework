package tech.kayys.wayang.harness.protocol;

import java.util.Optional;

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
