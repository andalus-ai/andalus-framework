package tech.kayys.wayang.harness.tool;

import java.util.Collection;
import java.util.Objects;
import java.util.Optional;

public class DefaultToolResolver implements ToolResolver {

    private final ToolCatalog catalog;
    private final ToolSelectionPolicy selectionPolicy;

    public DefaultToolResolver(ToolCatalog catalog, ToolSelectionPolicy selectionPolicy) {
        this.catalog = Objects.requireNonNull(catalog, "catalog cannot be null");
        this.selectionPolicy = selectionPolicy != null ? selectionPolicy : ToolSelectionPolicy.firstMatch();
    }

    public DefaultToolResolver(ToolCatalog catalog) {
        this(catalog, ToolSelectionPolicy.firstMatch());
    }

    @Override
    public Optional<ToolResolution> resolve(ToolIntent intent, ToolResolutionContext context) {
        if (intent == null) {
            return Optional.empty();
        }
        ToolDiscoveryRequest request = ToolDiscoveryRequest.forCapability(intent.capability());
        Collection<ToolDescriptor> candidates = catalog.discover(request);

        if (candidates.isEmpty()) {
            // Also check if capability is a direct tool id
            Optional<ToolDescriptor> direct = catalog.get(ToolId.of(intent.capability()));
            if (direct.isPresent()) {
                return Optional.of(new ToolResolution(direct.get(), ToolSelectionReason.direct("Direct tool ID match")));
            }
            return Optional.empty();
        }

        return selectionPolicy.select(intent, candidates, context)
                .map(tool -> new ToolResolution(tool, ToolSelectionReason.capability(intent.capability())));
    }
}
