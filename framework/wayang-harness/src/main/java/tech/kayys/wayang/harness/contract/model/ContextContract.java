package tech.kayys.wayang.harness.contract.model;

import java.util.Set;

/**
 * Contract specifying required and optional context dimensions (e.g. "repository", "task", "history").
 */
public record ContextContract(
        Set<String> requiredDimensions,
        Set<String> optionalDimensions
) {
    public ContextContract {
        requiredDimensions = requiredDimensions != null ? Set.copyOf(requiredDimensions) : Set.of();
        optionalDimensions = optionalDimensions != null ? Set.copyOf(optionalDimensions) : Set.of();
    }

    public static ContextContract of(Set<String> required, Set<String> optional) {
        return new ContextContract(required, optional);
    }

    public static ContextContract empty() {
        return new ContextContract(Set.of(), Set.of());
    }
}
