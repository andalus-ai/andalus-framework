package tech.kayys.wayang.execution.workspace;

import java.util.Objects;

/**
 * Specification for an explicit mount inside a sandbox workspace.
 */
public record MountSpec(
        String hostPath,
        String mountPath,
        MountMode mode
) {

    public MountSpec {
        Objects.requireNonNull(hostPath, "hostPath cannot be null");
        Objects.requireNonNull(mountPath, "mountPath cannot be null");
        mode = mode != null ? mode : MountMode.READ_WRITE;
    }

    public static MountSpec readWrite(String hostPath, String mountPath) {
        return new MountSpec(hostPath, mountPath, MountMode.READ_WRITE);
    }

    public static MountSpec readOnly(String hostPath, String mountPath) {
        return new MountSpec(hostPath, mountPath, MountMode.READ_ONLY);
    }
}
