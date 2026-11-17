package tech.kayys.andalus.execution;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;
import tech.kayys.andalus.tool.Tool;
import tech.kayys.andalus.tool.ToolInvocation;

/**
 * Pipeline for executing tools with policies.
 *
 * <p>Supports both sequential and parallel execution. When a model emits
 * multiple independent tool calls in a single turn, {@link #executeAll} runs
 * them concurrently and merges the decisions.</p>
 */
public interface AgentToolExecutor {

    /**
     * Evaluates and executes a single tool invocation.
     * May return {@link AgentDecision.WaitForApproval} or {@link AgentDecision.ExecuteTool}.
     *
     * @param invocation The requested tool invocation
     * @return Future containing the decision
     */
    CompletionStage<AgentDecision> execute(ToolInvocation invocation);

    /**
     * Evaluates and executes a tool invocation using the immutable context of
     * the owning agent execution.
     *
     * <p>Context-aware implementations must use this context for policy
     * evaluation, tenant-aware cache keys, budget accounting and audit events.
     * They must not recover identity from mutable executor fields or thread
     * locals. The default preserves source and binary compatibility for
     * existing implementations; implementations requiring identity isolation
     * should override this method.</p>
     *
     * @param invocation requested tool invocation
     * @param context immutable execution-scoped identity and policy snapshot
     * @return future containing the tool decision
     */
    default CompletionStage<AgentDecision> execute(
            ToolInvocation invocation,
            AgentToolExecutionContext context) {
        if (context == null) {
            return CompletableFuture.failedFuture(
                    new IllegalArgumentException("AgentToolExecutionContext is required"));
        }
        return execute(invocation);
    }

    /**
     * Executes a tool with an execution-scoped cooperative cancellation signal.
     *
     * <p>Implementations that can interrupt queued or active tool work should override
     * this method and propagate the signal to their execution context. The default
     * implementation checks cancellation before dispatch and after completion, but
     * cannot interrupt an already-running implementation of {@link #execute}.</p>
     *
     * @param invocation the requested tool invocation
     * @param cancellation execution-scoped cancellation signal
     * @return future containing the tool decision
     */
    default CompletionStage<AgentDecision> execute(
            ToolInvocation invocation,
            tech.kayys.andalus.tool.CancellationToken cancellation) {
        if (cancellation != null && cancellation.isCancellationRequested()) {
            return CompletableFuture.failedFuture(
                    new java.util.concurrent.CancellationException("Execution cancelled before tool dispatch."));
        }
        CompletionStage<AgentDecision> result = execute(invocation);
        if (cancellation == null) {
            return result;
        }
        return result.thenApply(decision -> {
            if (cancellation.isCancellationRequested()) {
                throw new java.util.concurrent.CancellationException("Execution cancelled during tool execution.");
            }
            return decision;
        });
    }

    /**
     * Executes multiple independent tool invocations in parallel.
     * If any invocation requires approval, the whole batch pauses.
     *
     * @param invocations List of independent tool invocations
     * @return Future containing a list of decisions (one per invocation, in order)
     */
    default CompletionStage<List<AgentDecision>> executeAll(List<ToolInvocation> invocations) {
        List<CompletableFuture<AgentDecision>> futures = invocations.stream()
            .map(inv -> execute(inv).toCompletableFuture())
            .collect(Collectors.toList());

        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
            .thenApply(v -> futures.stream()
                .map(CompletableFuture::join)
                .collect(Collectors.toList()));
    }

    /**
     * Optional interface that {@link AgentToolExecutor} implementations can implement
     * to expose the set of {@link Tool} instances they manage.
     *
     * <p>{@link DefaultAgentExecution} checks for this interface at execution time so
     * it can pass the registered tools to the underlying {@code ReActAgent} without a
     * direct dependency on the concrete executor class.</p>
     */
    interface ToolAware {
        /** Returns all tools that this executor can dispatch. Never {@code null}. */
        List<Tool> availableTools();
    }
}

