package tech.kayys.wayang.tool.routing;

import tech.kayys.wayang.tool.ToolInvocation;

/**
 * Resolves a {@link ToolInvocation} to a {@link ResolvedTool}.
 */
public interface ToolResolver {

    ResolvedTool resolve(ToolInvocation invocation);
}
