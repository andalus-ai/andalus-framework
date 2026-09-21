package tech.kayys.andalus.agent.spi.approval;

import tech.kayys.andalus.agent.Agent;
import tech.kayys.andalus.tool.ToolInvocation;

/**
 * SPI for intercepting tool calls to require human approval or check against guardrails.
 */
public interface ApprovalStrategy {

    /**
     * Called before a tool is executed.
     * 
     * @param agent the agent executing the tool
     * @param invocation the details of the tool being called
     * @throws ApprovalRequiredException if the tool execution must be paused for human review
     */
    void requestApproval(Agent agent, ToolInvocation invocation) throws ApprovalRequiredException;
}
