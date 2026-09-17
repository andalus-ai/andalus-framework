package tech.kayys.wayang.execution.governance;

import tech.kayys.wayang.tool.ToolInvocation;

import java.util.Objects;

/**
 * Standard implementation of {@link ToolAuthorizationService} delegating to {@link ToolPolicyEvaluator}.
 */
public final class DefaultToolAuthorizationService implements ToolAuthorizationService {

    private final ToolPolicyEvaluator evaluator;

    public DefaultToolAuthorizationService(ToolPolicyEvaluator evaluator) {
        this.evaluator = Objects.requireNonNull(evaluator, "evaluator cannot be null");
    }

    @Override
    public PolicyDecision authorize(ToolInvocation invocation, PolicyEvaluationContext context) {
        Objects.requireNonNull(invocation, "invocation cannot be null");
        Objects.requireNonNull(context, "context cannot be null");

        return evaluator.evaluate(context);
    }
}
