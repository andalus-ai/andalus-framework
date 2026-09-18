package tech.kayys.wayang.harness.model;

import java.util.Collection;
import java.util.Optional;

@FunctionalInterface
public interface ModelRoutingPolicy {

    Optional<ModelDescriptor> select(
            ModelIntent intent,
            Collection<ModelDescriptor> candidates,
            ModelRoutingContext context
    );

    static ModelRoutingPolicy localFirst() {
        return (intent, candidates, context) -> {
            // First look for local models
            Optional<ModelDescriptor> local = candidates.stream()
                    .filter(m -> m.metadata().local())
                    .findFirst();
            if (local.isPresent()) {
                return local;
            }
            // Fallback to any candidate
            return candidates.stream().findFirst();
        };
    }

    static ModelRoutingPolicy firstAvailable() {
        return (intent, candidates, context) -> candidates.stream().findFirst();
    }
}
