package tech.kayys.andalus.agent.builder;

import tech.kayys.andalus.agent.Agent;
import tech.kayys.andalus.agent.react.ReActAgent;
import tech.kayys.andalus.provider.ChatMessage;
import tech.kayys.andalus.provider.Provider;
import tech.kayys.andalus.provider.Provider;
import tech.kayys.andalus.spi.memory.Memory;
import tech.kayys.andalus.tool.Tool;
import tech.kayys.andalus.tool.ToolRegistry;
import tech.kayys.andalus.tool.capability.Capability;
import tech.kayys.andalus.tool.capability.CapabilityRequest;
import tech.kayys.andalus.context.api.BudgetedContextCompiler;
import tech.kayys.andalus.context.api.ContextPlanner;
import tech.kayys.andalus.agent.spi.AgentListener;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Fluent builder for Agent construction.
 */
public class AgentBuilder {

    private String type;
    private Provider provider;
    private Memory<ChatMessage> memory;
    private String modelId;
    private String systemPrompt;
    private List<Tool> explicitTools = new ArrayList<>();
    private ToolRegistry toolRegistry;
    private CapabilityRequest capabilityRequest = new CapabilityRequest();
    private boolean autoApproveTools = false;
    private Path workspace;
    private BudgetedContextCompiler contextCompiler;
    private ContextPlanner contextPlanner;
    private Integer tokenBudget;
    private List<AgentListener> listeners = new ArrayList<>();
    private tech.kayys.andalus.agent.react.BaseReActAgent.ToolExecutorBridge toolExecutorBridge;
    private tech.kayys.andalus.agent.react.BaseReActAgent.CheckpointBridge checkpointBridge;
    private String checkpointExecutionId;

    private AgentBuilder(String type) {
        this.type = type;
    }

    public static AgentBuilder create(String type) {
        return new AgentBuilder(type);
    }

    public AgentBuilder withProvider(Provider provider) {
        this.provider = provider;
        return this;
    }

    public AgentBuilder withMemory(Memory<ChatMessage> memory) {
        this.memory = memory;
        return this;
    }

    public AgentBuilder withModelId(String modelId) {
        this.modelId = modelId;
        return this;
    }

    public AgentBuilder withSystemPrompt(String prompt) {
        this.systemPrompt = prompt;
        return this;
    }

    public AgentBuilder withTools(List<Tool> tools) {
        this.explicitTools.addAll(tools);
        return this;
    }
    
    public AgentBuilder addTool(Tool tool) {
        this.explicitTools.add(tool);
        return this;
    }

    public AgentBuilder withToolRegistry(ToolRegistry registry) {
        this.toolRegistry = registry;
        return this;
    }

    public AgentBuilder withCapabilities(Capability... capabilities) {
        for (Capability cap : capabilities) {
            this.capabilityRequest.require(cap);
        }
        return this;
    }

    public AgentBuilder withAutoApproveTools(boolean autoApprove) {
        this.autoApproveTools = autoApprove;
        return this;
    }

    public AgentBuilder withWorkspace(Path workspace) {
        this.workspace = workspace;
        return this;
    }

    public AgentBuilder withContextCompiler(BudgetedContextCompiler compiler, ContextPlanner planner, int budget) {
        this.contextCompiler = compiler;
        this.contextPlanner = planner;
        this.tokenBudget = budget;
        return this;
    }

    public AgentBuilder addListener(AgentListener listener) {
        this.listeners.add(listener);
        return this;
    }

    public AgentBuilder withToolExecutor(tech.kayys.andalus.agent.react.BaseReActAgent.ToolExecutorBridge bridge) {
        this.toolExecutorBridge = bridge;
        return this;
    }

    public AgentBuilder withCheckpointBridge(
            tech.kayys.andalus.agent.react.BaseReActAgent.CheckpointBridge bridge, String executionId) {
        this.checkpointBridge = bridge;
        this.checkpointExecutionId = executionId;
        return this;
    }

    public Agent build() {
        Agent agent;
        if ("react".equalsIgnoreCase(type)) {
            agent = new ReActAgent();
        } else {
            // Further architectures (plan-and-solve) could be added here
            throw new IllegalArgumentException("Unsupported agent type: " + type);
        }

        List<Tool> resolvedTools = new ArrayList<>(this.explicitTools);
        if (toolRegistry != null && !capabilityRequest.getRequiredCapabilities().isEmpty()) {
            resolvedTools.addAll(toolRegistry.getToolsByCapability(capabilityRequest));
        }

        agent.setProvider(provider);
        agent.setMemory(memory);
        agent.setModelId(modelId);
        agent.setSystemPrompt(systemPrompt);
        agent.setTools(resolvedTools);
        agent.setAutoApproveTools(autoApproveTools);
        
        if (agent instanceof tech.kayys.andalus.agent.react.BaseReActAgent base) {
            base.setWorkspace(workspace);
            if (contextCompiler != null && tokenBudget != null) {
                base.setContextCompiler(contextCompiler, contextPlanner, tokenBudget);
            }
            base.setListeners(listeners);
            if (toolExecutorBridge != null) {
                base.setToolExecutor(toolExecutorBridge);
            }
            if (checkpointBridge != null && checkpointExecutionId != null) {
                base.setCheckpointBridge(checkpointBridge, checkpointExecutionId);
            }
        }

        return agent;
    }
}
