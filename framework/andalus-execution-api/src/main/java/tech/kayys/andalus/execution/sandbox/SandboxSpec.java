package tech.kayys.andalus.execution.sandbox;

import tech.kayys.andalus.execution.filesystem.FilesystemPolicy;
import tech.kayys.andalus.execution.limits.ResourceLimits;
import tech.kayys.andalus.execution.network.NetworkPolicy;
import tech.kayys.andalus.execution.process.ProcessPolicy;
import tech.kayys.andalus.execution.secrets.SecretPolicy;
import tech.kayys.andalus.execution.workspace.WorkspaceSpec;

import java.util.Objects;

/**
 * Central declarative contract defining the security and isolation boundary of a sandbox.
 */
public record SandboxSpec(
        WorkspaceSpec workspace,
        FilesystemPolicy filesystem,
        ProcessPolicy process,
        NetworkPolicy network,
        SecretPolicy secrets,
        ResourceLimits resources,
        SandboxLifecycle lifecycle
) {

    public SandboxSpec {
        workspace = workspace != null ? workspace : WorkspaceSpec.ephemeral();
        filesystem = filesystem != null ? filesystem : FilesystemPolicy.readWrite();
        process = process != null ? process : ProcessPolicy.allowAll();
        network = network != null ? network : NetworkPolicy.disabled();
        secrets = secrets != null ? secrets : SecretPolicy.denyAll();
        resources = resources != null ? resources : ResourceLimits.standard();
        lifecycle = lifecycle != null ? lifecycle : SandboxLifecycle.standard();
    }
}
