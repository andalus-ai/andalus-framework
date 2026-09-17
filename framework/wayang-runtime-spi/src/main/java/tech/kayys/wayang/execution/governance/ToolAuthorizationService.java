package tech.kayys.wayang.execution.governance;

import tech.kayys.wayang.tool.ToolInvocation;

/**
 * Public authorization service entry point for tool invocations.
 */
public interface ToolAuthorizationService {

    PolicyDecision authorize(ToolInvocation invocation, PolicyEvaluationContext context);
}
