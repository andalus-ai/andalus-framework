package tech.kayys.wayang.execution.core.environment;

import tech.kayys.wayang.execution.environment.EnvironmentCapabilities;
import tech.kayys.wayang.execution.environment.EnvironmentState;
import tech.kayys.wayang.execution.environment.EnvironmentType;
import tech.kayys.wayang.execution.environment.ExecutionEnvironment;
import tech.kayys.wayang.execution.environment.ExecutionEnvironmentId;
import tech.kayys.wayang.execution.environment.ResourceAllocation;
import tech.kayys.wayang.execution.workspace.WorkspaceHandle;

import java.util.Objects;

/**
 * Standard implementation of {@link ExecutionEnvironment}.
 */
public record DefaultExecutionEnvironment(
        ExecutionEnvironmentId id,
        EnvironmentType type,
        EnvironmentCapabilities capabilities,
        EnvironmentState state,
        WorkspaceHandle workspace,
        ResourceAllocation allocation
) implements ExecutionEnvironment {

    public DefaultExecutionEnvironment {
        Objects.requireNonNull(id, "ExecutionEnvironmentId cannot be null");
        Objects.requireNonNull(type, "EnvironmentType cannot be null");
        capabilities = capabilities != null ? capabilities : EnvironmentCapabilities.defaults();
        state = state != null ? state : EnvironmentState.READY;
        allocation = allocation != null ? allocation : ResourceAllocation.unconstrained();
    }

    public static DefaultExecutionEnvironment inProcess(ExecutionEnvironmentId id, WorkspaceHandle workspace) {
        return new DefaultExecutionEnvironment(
                id,
                EnvironmentType.IN_PROCESS,
                EnvironmentCapabilities.defaults(),
                EnvironmentState.ACTIVE,
                workspace,
                ResourceAllocation.unconstrained()
        );
    }

    public static DefaultExecutionEnvironment sandbox(ExecutionEnvironmentId id, WorkspaceHandle workspace) {
        return new DefaultExecutionEnvironment(
                id,
                EnvironmentType.SANDBOX,
                EnvironmentCapabilities.restricted(),
                EnvironmentState.READY,
                workspace,
                ResourceAllocation.unconstrained()
        );
    }
}
