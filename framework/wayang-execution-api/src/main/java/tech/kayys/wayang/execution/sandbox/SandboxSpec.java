package tech.kayys.wayang.execution.sandbox;

import tech.kayys.wayang.execution.filesystem.FilesystemPolicy;
import tech.kayys.wayang.execution.limits.ResourceLimits;
import tech.kayys.wayang.execution.network.NetworkPolicy;
import tech.kayys.wayang.execution.process.ProcessPolicy;
import tech.kayys.wayang.execution.secrets.SecretPolicy;
import tech.kayys.wayang.execution.workspace.WorkspaceSpec;

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
