package tech.kayys.andalus.execution.backend;

import tech.kayys.andalus.execution.sandbox.ExecutionSandbox;
import tech.kayys.andalus.execution.sandbox.SandboxId;

/**
 * Backend interface for container-isolated sandbox execution (Docker, Podman, OCI).
 */
public interface ContainerBackend {

    String createContainer(ExecutionSandbox sandbox) throws Exception;

    void startContainer(String containerId) throws Exception;

    void stopContainer(String containerId) throws Exception;

    void removeContainer(String containerId) throws Exception;
}
