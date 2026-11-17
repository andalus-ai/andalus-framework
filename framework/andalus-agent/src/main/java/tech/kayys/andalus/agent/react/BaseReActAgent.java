package tech.kayys.andalus.agent.react;

import tech.kayys.andalus.agent.Agent;
import tech.kayys.andalus.agent.PermissionDecision;
import tech.kayys.andalus.agent.AndalusAgentListener;
import tech.kayys.andalus.provider.ChatMessage;
import tech.kayys.andalus.provider.Provider;
import tech.kayys.andalus.spi.memory.Memory;
import tech.kayys.andalus.tool.Tool;
import tech.kayys.andalus.tool.ToolInvocation;
import tech.kayys.andalus.tool.ToolResult;
import tech.kayys.andalus.context.api.BudgetedContextCompiler;
import tech.kayys.andalus.context.api.ContextPlanner;
import tech.kayys.andalus.agent.spi.AgentListener;

import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.BiConsumer;
import java.util.function.Supplier;
import java.util.function.Consumer;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.io.IOException;

/**
 * A base implementation of a Reasoning and Acting (ReAct) agent.
 */
public abstract class BaseReActAgent implements Agent {
    protected Provider provider;
    protected Memory<ChatMessage> memory;
    protected String modelId;
    protected String systemPrompt;
    protected List<Tool> tools = new ArrayList<>();
    protected boolean autoApproveTools = false;
    protected Path workspace;

    protected tech.kayys.andalus.tool.ToolContext toolContext;

    /** Cooperative cancellation signal shared with the owning execution. */
    protected tech.kayys.andalus.tool.CancellationToken cancellationToken =
            tech.kayys.andalus.tool.CancellationToken.none();
    
    protected BudgetedContextCompiler contextCompiler;
    protected ContextPlanner contextPlanner;
    protected Integer tokenBudget;

    /** Optional: policy-aware tool executor (pipeline: validate → CB → retry → execute). */
    protected ToolExecutorBridge toolExecutor;

    /** Optional: per-step checkpoint persistence. */
    protected CheckpointBridge checkpointBridge;

    /** Execution ID used for checkpoint keys. */
    protected String executionId;

    protected List<AgentListener> listeners = new ArrayList<>();

    // -------------------------------------------------------------------------
    // Minimal bridge interfaces — avoids hard compile-time dep on andalus-runtime-spi
    // from the framework module.
    // -------------------------------------------------------------------------

    /** Functional bridge for executing a tool through the policy pipeline. */
    @FunctionalInterface
    public interface ToolExecutorBridge {
        /** Returns a ToolResult or throws on failure. */
        ToolResult executeViaPolicy(ToolInvocation invocation, Supplier<ToolResult> directFallback) throws Exception;

        /**
         * Cancellation-aware policy execution hook. Policy executors should override
         * this method to propagate cancellation into queued or active governed work.
         */
        default ToolResult executeViaPolicy(
                ToolInvocation invocation,
                Supplier<ToolResult> directFallback,
                tech.kayys.andalus.tool.CancellationToken cancellation) throws Exception {
            if (cancellation != null && cancellation.isCancellationRequested()) {
                throw new java.util.concurrent.CancellationException("Tool execution cancelled before dispatch.");
            }
            ToolResult result = executeViaPolicy(invocation, directFallback);
            if (cancellation != null && cancellation.isCancellationRequested()) {
                throw new java.util.concurrent.CancellationException("Tool execution cancelled.");
            }
            return result;
        }
    }

    /** Functional bridge for saving a checkpoint after each step. */
    @FunctionalInterface
    public interface CheckpointBridge {
        void save(String executionId, tech.kayys.andalus.agent.AgentContext context);
    }

    @Override
    public Memory<ChatMessage> getMemory() {
        return memory;
    }

    @Override
    public void setMemory(Memory<ChatMessage> memory) {
        this.memory = memory;
    }

    @Override
    public void setProvider(Provider provider) {
        this.provider = provider;
    }

    @Override
    public void setModelId(String modelId) {
        this.modelId = modelId;
    }

    @Override
    public String getModelId() {
        return modelId;
    }

    @Override
    public void setSystemPrompt(String sp) {
        this.systemPrompt = sp;
    }

    @Override
    public Collection<Tool> tools() {
        return tools;
    }

    @Override
    public void setTools(List<Tool> tools) {
        this.tools.clear();
        this.tools.addAll(tools);
    }

    @Override
    public boolean autoApproveTools() {
        return autoApproveTools;
    }

    @Override
    public void setAutoApproveTools(boolean v) {
        this.autoApproveTools = v;
    }

    @Override
    public Path workspace() {
        return workspace;
    }
    
    public void setWorkspace(Path workspace) {
        this.workspace = workspace;
    }

    public void setContextCompiler(BudgetedContextCompiler compiler, ContextPlanner planner, Integer budget) {
        this.contextCompiler = compiler;
        this.contextPlanner = planner;
        this.tokenBudget = budget;
    }

    public void setToolExecutor(ToolExecutorBridge toolExecutor) {
        this.toolExecutor = toolExecutor;
    }

    /**
     * Runs a provider stream through the cancellable provider contract and waits
     * synchronously for completion to preserve the existing agent API.
     *
     * <p>Cancellation is polled while waiting, and the provider handle is
     * cancelled before returning control to the caller. Event delivery is
     * suppressed once the execution token has been cancelled.</p>
     */
    protected void streamChatCancellable(
            List<ChatMessage> messages,
            String prompt,
            List<tech.kayys.andalus.provider.ToolSpec> toolSpecs,
            double temperature,
            int maxTokens,
            Consumer<tech.kayys.andalus.provider.StreamEvent> onEvent)
            throws IOException, InterruptedException {
        throwIfCancelled();
        tech.kayys.andalus.provider.ProviderStreamHandle stream =
                provider.streamChatCancellable(messages, prompt, toolSpecs, temperature, maxTokens, event -> {
                    if (!isCancellationRequested()) {
                        onEvent.accept(event);
                    }
                });
        try {
            while (true) {
                if (isCancellationRequested()) {
                    stream.cancel();
                    throw new CancellationException("Agent execution cancelled.");
                }
                try {
                    stream.completion().toCompletableFuture().get(50, TimeUnit.MILLISECONDS);
                    return;
                } catch (TimeoutException waiting) {
                    // Re-check cancellation without busy-waiting.
                } catch (ExecutionException failed) {
                    Throwable cause = failed.getCause();
                    if (cause instanceof IOException io) throw io;
                    if (cause instanceof InterruptedException interrupted) throw interrupted;
                    if (cause instanceof CancellationException cancelled) throw cancelled;
                    if (cause instanceof RuntimeException runtime) throw runtime;
                    throw new IOException("Provider stream failed.", cause);
                }
            }
        } catch (InterruptedException interrupted) {
            stream.cancel();
            throw interrupted;
        } finally {
            if (isCancellationRequested()) {
                stream.cancel();
            }
        }
    }

    /** Returns whether the owning execution has requested cancellation. */
    protected boolean isCancellationRequested() {
        return cancellationToken != null && cancellationToken.isCancellationRequested();
    }

    /** Fails fast at an agent phase or tool-dispatch boundary after cancellation. */
    protected void throwIfCancelled() {
        if (isCancellationRequested()) {
            throw new CancellationException("Agent execution cancelled.");
        }
    }

    /** Sets the execution-scoped cancellation signal. */
    public void setCancellationToken(tech.kayys.andalus.tool.CancellationToken cancellationToken) {
        this.cancellationToken = cancellationToken != null
                ? cancellationToken
                : tech.kayys.andalus.tool.CancellationToken.none();
    }

    public void setToolContext(
            tech.kayys.andalus.tool.ToolContext toolContext
    ) {
        this.toolContext = toolContext;
    }

    public tech.kayys.andalus.tool.ToolContext toolContext() {
        return toolContext;
    }

    public void setCheckpointBridge(CheckpointBridge bridge, String executionId) {
        this.checkpointBridge = bridge;
        this.executionId = executionId;
    }

    public void setListeners(List<AgentListener> listeners) {
        if (listeners != null) {
            this.listeners.addAll(listeners);
        }
    }
    
    protected void fireAgentStart(String sessionId) {
        listeners.forEach(l -> l.onAgentStart(this, sessionId));
    }
    
    protected void fireThought(String thought) {
        listeners.forEach(l -> l.onThought(this, thought));
    }
    
    protected void fireToolStart(ToolInvocation invocation) {
        listeners.forEach(l -> l.onToolStart(this, invocation));
    }
    
    protected void fireToolResult(ToolInvocation invocation, ToolResult result) {
        listeners.forEach(l -> l.onToolResult(this, invocation, result));
    }
    
    protected void fireAgentEnd(String finalResponse) {
        listeners.forEach(l -> l.onAgentEnd(this, finalResponse));
    }
    
    protected void fireAgentError(Throwable error) {
        listeners.forEach(l -> l.onAgentError(this, error));
    }
}
