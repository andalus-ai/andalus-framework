package tech.kayys.wayang.execution.environment;

import tech.kayys.wayang.execution.workspace.WorkspaceHandle;

/**
 * Fundamental execution environment boundary within which work executes.
 */
public interface ExecutionEnvironment {

    ExecutionEnvironmentId id();

    EnvironmentType type();

    EnvironmentCapabilities capabilities();

    EnvironmentState state();

    WorkspaceHandle workspace();

    ResourceAllocation allocation();
}
