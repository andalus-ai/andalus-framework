package tech.kayys.wayang.harness.model;

import java.util.List;
import java.util.Optional;

public record StopConditions(
        List<String> stopSequences,
        Optional<Integer> maxTokens
) {
    public StopConditions {
        stopSequences = stopSequences != null ? List.copyOf(stopSequences) : List.of();
        if (maxTokens == null) {
            maxTokens = Optional.empty();
        }
    }

    public static StopConditions none() {
        return new StopConditions(List.of(), Optional.empty());
    }

    public static StopConditions of(List<String> stopSequences) {
        return new StopConditions(stopSequences, Optional.empty());
    }
}
