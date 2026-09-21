package tech.kayys.andalus.harness.model;

import tech.kayys.andalus.harness.context.HarnessIdentity;
import tech.kayys.andalus.harness.execution.state.ExecutionId;
import tech.kayys.andalus.harness.resource.ResourceScope;
import tech.kayys.andalus.tool.CancellationToken;
import tech.kayys.andalus.tool.ToolLogger;

/**
 * Defines the contract for model execution context operations in the Andalus framework.
 */


public interface ModelExecutionContext {

    ExecutionId executionId();

    HarnessIdentity identity();

    ResourceScope resources();

    CancellationToken cancellation();

    ToolLogger logger();
}
