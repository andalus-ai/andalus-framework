package tech.kayys.andalus.tool;

import tech.kayys.andalus.tool.artifact.ArtifactStore;

/**
 * Context provided during tool execution containing runtime dependencies and control primitives.
 */
public interface ToolExecutionContext extends ToolContext {

    String executionId();

    CancellationToken cancellation();

    ToolLogger logger();

    ArtifactStore artifacts();

    default Object identity() {
        return null;
    }

    default Object resources() {
        return null;
    }
}
