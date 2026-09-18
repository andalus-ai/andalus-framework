package tech.kayys.wayang.harness.tool;

import tech.kayys.wayang.harness.context.HarnessIdentity;
import tech.kayys.wayang.harness.execution.state.ExecutionId;
import tech.kayys.wayang.harness.resource.ResourceScope;

public interface ToolExecutionContext {

    ExecutionId executionId();

    HarnessIdentity identity();

    ResourceScope resources();

    CancellationToken cancellation();

    ToolLogger logger();

    ArtifactStore artifacts();
}
