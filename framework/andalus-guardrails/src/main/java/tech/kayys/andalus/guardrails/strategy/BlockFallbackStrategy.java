package tech.kayys.andalus.guardrails.strategy;

import tech.kayys.andalus.agent.Agent;
import tech.kayys.andalus.agent.spi.approval.ApprovalRequiredException;
import tech.kayys.andalus.guardrails.ExecutionResult;
import tech.kayys.andalus.tool.ToolInvocation;

/**
 * A fallback strategy that strictly blocks the execution by throwing a SecurityException.
 */
public class BlockFallbackStrategy implements GuardrailFallbackStrategy {

    @Override
    public void handleViolation(Agent agent, ToolInvocation invocation, ExecutionResult result) throws ApprovalRequiredException {
        throw new SecurityException("Guardrail violation: Execution blocked by policy. Details: " + result.toString());
    }
}
