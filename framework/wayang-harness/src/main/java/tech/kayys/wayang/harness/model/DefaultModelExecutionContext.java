package tech.kayys.wayang.harness.model;

import tech.kayys.wayang.harness.context.HarnessIdentity;
import tech.kayys.wayang.harness.execution.state.ExecutionId;
import tech.kayys.wayang.harness.resource.ResourceScope;
import tech.kayys.wayang.tool.CancellationToken;
import tech.kayys.wayang.tool.ToolLogger;

/**
 * Represents a default model execution context.
 *
 * <p>Its components capture `execution id`, `identity`, `resources`, `cancellation`, `logger`.</p>
 *
 * @param executionId the execution id
 * @param identity the identity
 * @param resources the resources
 * @param cancellation the cancellation
 * @param logger the logger
 */


public record DefaultModelExecutionContext(
        ExecutionId executionId,
        HarnessIdentity identity,
        ResourceScope resources,
        CancellationToken cancellation,
        ToolLogger logger
) implements ModelExecutionContext {

    public DefaultModelExecutionContext {
        if (executionId == null) executionId = ExecutionId.generate();
        if (cancellation == null) cancellation = CancellationToken.none();
        if (logger == null) logger = ToolLogger.noop();
    }

    public static DefaultModelExecutionContext of(ExecutionId executionId, HarnessIdentity identity, ResourceScope resources) {
        return new DefaultModelExecutionContext(executionId, identity, resources, CancellationToken.create(), ToolLogger.noop());
    }
}
