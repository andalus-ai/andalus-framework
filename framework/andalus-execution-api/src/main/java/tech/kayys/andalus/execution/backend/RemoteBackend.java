package tech.kayys.andalus.execution.backend;

import tech.kayys.andalus.execution.environment.ExecutionEnvironmentId;
import tech.kayys.andalus.execution.sandbox.SandboxId;

/**
 * Backend interface for dispatching execution tasks to remote workers or clusters.
 */
public interface RemoteBackend {

    void dispatch(ExecutionEnvironmentId environmentId, SandboxId sandboxId, String taskPayload) throws Exception;

    boolean isAvailable(ExecutionEnvironmentId environmentId);
}
