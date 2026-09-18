package tech.kayys.wayang.harness.model;

import tech.kayys.wayang.harness.context.HarnessIdentity;
import tech.kayys.wayang.harness.execution.state.ExecutionId;
import tech.kayys.wayang.harness.resource.ResourceScope;
import tech.kayys.wayang.tool.CancellationToken;
import tech.kayys.wayang.tool.ToolLogger;

/**
 * Defines the contract for model execution context operations in the Wayang framework.
 */


public interface ModelExecutionContext {

    ExecutionId executionId();

    HarnessIdentity identity();

    ResourceScope resources();

    CancellationToken cancellation();

    ToolLogger logger();
}
