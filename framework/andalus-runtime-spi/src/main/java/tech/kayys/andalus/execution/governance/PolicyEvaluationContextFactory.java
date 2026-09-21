package tech.kayys.andalus.execution.governance;

import tech.kayys.andalus.tool.ToolContext;
import tech.kayys.andalus.tool.ToolInvocation;

/**
 * Factory for creating a {@link PolicyEvaluationContext} from runtime tool context and invocation.
 */
public interface PolicyEvaluationContextFactory {

    PolicyEvaluationContext create(ToolInvocation invocation, ToolContext toolContext);
}
