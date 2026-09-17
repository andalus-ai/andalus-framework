package tech.kayys.wayang.execution.governance;

import tech.kayys.wayang.tool.ToolContext;
import tech.kayys.wayang.tool.ToolInvocation;

/**
 * Factory for creating a {@link PolicyEvaluationContext} from runtime tool context and invocation.
 */
public interface PolicyEvaluationContextFactory {

    PolicyEvaluationContext create(ToolInvocation invocation, ToolContext toolContext);
}
