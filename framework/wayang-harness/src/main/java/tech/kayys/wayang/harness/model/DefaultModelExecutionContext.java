package tech.kayys.wayang.harness.model;

import tech.kayys.wayang.harness.context.HarnessIdentity;
import tech.kayys.wayang.harness.execution.state.ExecutionId;
import tech.kayys.wayang.harness.resource.ResourceScope;
import tech.kayys.wayang.harness.tool.CancellationToken;
import tech.kayys.wayang.harness.tool.ToolLogger;

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
