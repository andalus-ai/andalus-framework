package tech.kayys.wayang.harness.model;

import java.time.Duration;
import java.util.Optional;

/**
 * Represents a model requirements.
 *
 * <p>Its components capture `task`, `local preferred`, `streaming required`, `tool calling required`, `minimum context tokens`, and other values.</p>
 *
 * @param task the task
 * @param localPreferred the local preferred
 * @param streamingRequired the streaming required
 * @param toolCallingRequired the tool calling required
 * @param minimumContextTokens the minimum context tokens
 * @param maxLatency the max latency
 */


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
