package tech.kayys.wayang.tool.routing;

import tech.kayys.wayang.execution.governance.PolicyEvaluationContext;

import java.util.Objects;

/**
 * Context provided to {@link ToolProviderSelector} during tool provider resolution.
 */
public record ToolRoutingContext(
        PolicyEvaluationContext policyContext
) {

    public ToolRoutingContext {
        Objects.requireNonNull(policyContext, "policyContext cannot be null");
    }
}
