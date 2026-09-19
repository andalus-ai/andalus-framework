package tech.kayys.wayang.execution.core.environment;

import tech.kayys.wayang.execution.core.workspace.LocalWorkspace;
import tech.kayys.wayang.execution.environment.*;
import tech.kayys.wayang.execution.workspace.WorkspaceHandle;
import tech.kayys.wayang.execution.workspace.WorkspaceId;
import tech.kayys.wayang.execution.workspace.WorkspaceMode;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Standard provider for execution environments.
 */
public class DefaultExecutionEnvironmentProvider implements ExecutionEnvironmentProvider {

    private final Map<ExecutionEnvironmentId, ExecutionEnvironment> activeEnvironments = new ConcurrentHashMap<>();

    @Override
    public ExecutionEnvironment provision(ExecutionEnvironmentRequest request) {
        Objects.requireNonNull(request, "ExecutionEnvironmentRequest cannot be null");
        ExecutionEnvironmentId envId = request.requestedId();

        try {
            Path tempDir = Files.createTempDirectory("wayang-env-" + envId.value());
            WorkspaceHandle workspace = new LocalWorkspace(
                    WorkspaceId.of("ws-" + envId.value()),
                    request.workspaceSpec() != null ? request.workspaceSpec().mode() : WorkspaceMode.EPHEMERAL,
                    tempDir
            );

            ExecutionEnvironment env = new DefaultExecutionEnvironment(
                    envId,
                    request.preferredType(),
                    request.requiredCapabilities(),
                    EnvironmentState.READY,
                    workspace,
                    ResourceAllocation.unconstrained()
            );

            activeEnvironments.put(envId, env);
            return env;
        } catch (Exception e) {
            throw new IllegalStateException("Failed to provision execution environment: " + envId.value(), e);
        }
    }

    @Override
    public void release(ExecutionEnvironmentId environmentId) {
        ExecutionEnvironment env = activeEnvironments.remove(environmentId);
        if (env != null && env.workspace() != null) {
            try {
                env.workspace().close();
            } catch (Exception ignored) {}
        }
    }
}
