package tech.kayys.andalus.guardrails.strategy;

import tech.kayys.andalus.agent.Agent;
import tech.kayys.andalus.agent.spi.approval.ApprovalRequiredException;
import tech.kayys.andalus.guardrails.ExecutionResult;
import tech.kayys.andalus.tool.ToolInvocation;

/**
 * Immediately blocks the tool execution and throws an exception, aborting the agent's action.
 */
public class HardBlockFallbackStrategy implements GuardrailFallbackStrategy {

    @Override
    public void handleViolation(Agent agent, ToolInvocation invocation, ExecutionResult result) throws ApprovalRequiredException {
        throw new SecurityException("Action blocked by Guardrails: " + result.toString());
    }
}
