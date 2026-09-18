package tech.kayys.wayang.harness.model;

import java.time.Duration;
import java.util.Optional;

public record ModelRequirements(
        ModelTask task,
        boolean localPreferred,
        boolean streamingRequired,
        boolean toolCallingRequired,
        long minimumContextTokens,
        Optional<Duration> maxLatency
) {
    public ModelRequirements {
        if (task == null) task = ModelTask.CHAT;
        if (maxLatency == null) maxLatency = Optional.empty();
    }

    public static ModelRequirements chat() {
        return new ModelRequirements(ModelTask.CHAT, false, false, false, 8000, Optional.empty());
    }

    public static ModelRequirements localFirst() {
        return new ModelRequirements(ModelTask.CHAT, true, false, false, 8000, Optional.empty());
    }
}
