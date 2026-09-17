package tech.kayys.wayang.tool.routing;

import java.util.List;
import java.util.Objects;

/**
 * Provider selector filtering candidates by health/availability.
 */
public final class HealthAwareToolProviderSelector implements ToolProviderSelector {

    private final ToolProviderAvailability availability;

    public HealthAwareToolProviderSelector(ToolProviderAvailability availability) {
        this.availability = Objects.requireNonNull(availability, "availability cannot be null");
    }

    @Override
    public ResolvedTool select(List<ResolvedTool> candidates, ToolRoutingContext context) {
        Objects.requireNonNull(candidates, "candidates cannot be null");
        Objects.requireNonNull(context, "context cannot be null");

        for (ResolvedTool candidate : candidates) {
            if (availability.available(candidate.providerId())) {
                return candidate;
            }
        }

        throw new ToolRoutingException("No healthy tool provider is available");
    }
}
