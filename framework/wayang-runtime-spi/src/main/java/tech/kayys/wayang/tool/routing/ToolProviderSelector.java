package tech.kayys.wayang.tool.routing;

import java.util.List;

/**
 * Strategy interface for selecting among multiple candidates for a tool.
 */
public interface ToolProviderSelector {

    ResolvedTool select(List<ResolvedTool> candidates, ToolRoutingContext context);
}
