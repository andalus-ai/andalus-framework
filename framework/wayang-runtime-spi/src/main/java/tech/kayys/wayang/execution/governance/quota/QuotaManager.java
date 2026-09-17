package tech.kayys.wayang.execution.governance.quota;

import tech.kayys.wayang.execution.governance.PolicyEvaluationContext;
import tech.kayys.wayang.tool.ToolInvocation;

/**
 * Coordinates budget and rate checks prior to and following tool execution.
 */
public interface QuotaManager {

    /**
     * Checks rate limits and budget availability before invocation.
     *
     * @param invocation The tool invocation.
     * @param context    The policy evaluation context containing tenant/user information.
     * @throws QuotaExceededException if rate limit or call budget is exceeded.
     */
    void acquirePermit(ToolInvocation invocation, PolicyEvaluationContext context);

    /**
     * Records resource consumption (duration and cost) after tool execution.
     *
     * @param invocation The tool invocation.
     * @param context    The policy evaluation context.
     * @param durationMs Execution duration in milliseconds.
     * @param costUsd    Incurred execution cost in USD.
     */
    void recordConsumption(ToolInvocation invocation, PolicyEvaluationContext context, long durationMs, double costUsd);

    /**
     * Default no-op quota manager.
     */
    static QuotaManager noop() {
        return new QuotaManager() {
            @Override
            public void acquirePermit(ToolInvocation invocation, PolicyEvaluationContext context) {}
            @Override
            public void recordConsumption(ToolInvocation invocation, PolicyEvaluationContext context, long durationMs, double costUsd) {}
        };
    }
}
