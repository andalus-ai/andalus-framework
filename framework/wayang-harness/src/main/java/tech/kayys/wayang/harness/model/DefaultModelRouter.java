package tech.kayys.wayang.harness.model;

import java.util.*;

/**
 * Provides the default implementation of the model router contract.
 */


public class DefaultModelRouter implements ModelRouter {

    private final ModelCatalog catalog;
    private final ModelRoutingPolicy routingPolicy;

    public DefaultModelRouter(ModelCatalog catalog, ModelRoutingPolicy routingPolicy) {
        this.catalog = Objects.requireNonNull(catalog, "catalog cannot be null");
        this.routingPolicy = routingPolicy != null ? routingPolicy : ModelRoutingPolicy.localFirst();
    }

    public DefaultModelRouter(ModelCatalog catalog) {
        this(catalog, ModelRoutingPolicy.localFirst());
    }

    @Override
    public Optional<ModelResolution> resolve(ModelIntent intent, ModelRoutingContext context) {
        if (intent == null) return Optional.empty();

        Collection<ModelDescriptor> candidates = catalog.discover(intent.task());
        if (candidates.isEmpty()) {
            return Optional.empty();
        }

        // Filter by requirements
        ModelRequirements req = intent.requirements();
        List<ModelDescriptor> eligible = candidates.stream()
                .filter(m -> {
                    if (req.toolCallingRequired() && !m.capabilities().toolCalling()) return false;
                    if (req.streamingRequired() && !m.capabilities().streaming()) return false;
                    if (req.minimumContextTokens() > 0 && m.limits().maxContextTokens() < req.minimumContextTokens()) return false;
                    return true;
                })
                .toList();

        if (eligible.isEmpty()) {
            return Optional.empty();
        }

        return routingPolicy.select(intent, eligible, context)
                .map(model -> {
                    String explanation = model.metadata().local() ? "Selected local model" : "Selected cloud model";
                    ResolutionReason reason = model.metadata().local()
                            ? ResolutionReason.localFirst(explanation)
                            : ResolutionReason.cloudFallback(explanation);
                    return new ModelResolution(model, reason);
                });
    }
}
