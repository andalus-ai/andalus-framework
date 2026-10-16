package tech.kayys.andalus.execution.tree;

import tech.kayys.andalus.execution.AgentInvocation;

import java.util.concurrent.CompletionStage;

/**
 * Creates and manages delegated child executions.
 */
public interface ChildExecutionManager {

    /**
     * Creates and starts a child execution.
     */
    CompletionStage<ChildExecutionResult> execute(
            AgentInvocation parent,
            ChildExecutionRequest request
    );

    /**
     * Creates a child invocation without starting it.
     */
    AgentInvocation create(
            AgentInvocation parent,
            ChildExecutionRequest request
    );
}
