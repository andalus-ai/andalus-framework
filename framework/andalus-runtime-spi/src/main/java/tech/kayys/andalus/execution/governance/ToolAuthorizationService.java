package tech.kayys.andalus.execution.governance;

import tech.kayys.andalus.tool.ToolInvocation;

/**
 * Public authorization service entry point for tool invocations.
 */
public interface ToolAuthorizationService {

    PolicyDecision authorize(ToolInvocation invocation, PolicyEvaluationContext context);
}
