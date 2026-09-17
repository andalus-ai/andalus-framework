package tech.kayys.wayang.tool.routing;

import java.util.List;
import java.util.Objects;

/**
 * Strategy returning the first available tool provider from candidates.
 */
public final class FirstAvailableToolProviderSelector implements ToolProviderSelector {

    @Override
    public ResolvedTool select(List<ResolvedTool> candidates, ToolRoutingContext context) {
        Objects.requireNonNull(candidates, "candidates cannot be null");
        Objects.requireNonNull(context, "context cannot be null");

        if (candidates.isEmpty()) {
            throw new ToolRoutingException("No tool providers are available");
        }

        return candidates.getFirst();
    }
}
