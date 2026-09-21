package tech.kayys.andalus.tool.routing;

import tech.kayys.andalus.tool.ToolInvocation;

/**
 * Resolves a {@link ToolInvocation} to a {@link ResolvedTool}.
 */
public interface ToolResolver {

    ResolvedTool resolve(ToolInvocation invocation);
}
