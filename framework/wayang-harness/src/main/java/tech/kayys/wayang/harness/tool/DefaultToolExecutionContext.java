package tech.kayys.wayang.harness.tool;

import tech.kayys.wayang.harness.context.HarnessIdentity;
import tech.kayys.wayang.harness.execution.state.ExecutionId;
import tech.kayys.wayang.harness.resource.ResourceScope;

import java.util.Objects;

/**
 * Represents a default tool execution context.
 *
 * <p>Its components capture `execution id`, `identity`, `resources`, `cancellation`, `logger`, and other values.</p>
 *
 * @param executionId the execution id
 * @param identity the identity
 * @param resources the resources
 * @param cancellation the cancellation
 * @param logger the logger
 * @param artifacts the artifacts
 */


public record DefaultToolExecutionContext(
        ExecutionId executionId,
        HarnessIdentity identity,
        ResourceScope resources,
        CancellationToken cancellation,
        ToolLogger logger,
        ArtifactStore artifacts
) implements ToolExecutionContext {

    public DefaultToolExecutionContext {
        if (executionId == null) {
            executionId = ExecutionId.generate();
        }
        if (cancellation == null) {
            cancellation = CancellationToken.none();
        }
        if (logger == null) {
            logger = ToolLogger.noop();
        }
        if (artifacts == null) {
            artifacts = ArtifactStore.inMemory();
        }
    }

    public static DefaultToolExecutionContext of(
            ExecutionId executionId,
            HarnessIdentity identity,
            ResourceScope resources
    ) {
        return new DefaultToolExecutionContext(
                executionId,
                identity,
                resources,
                CancellationToken.create(),
                ToolLogger.noop(),
                ArtifactStore.inMemory()
        );
    }
}
