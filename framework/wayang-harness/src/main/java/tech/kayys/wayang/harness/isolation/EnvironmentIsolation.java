package tech.kayys.wayang.harness.isolation;

import java.util.Objects;

/**
 * Composite isolation profile governing filesystem, network, and process permissions.
 */
public record EnvironmentIsolation(
        FilesystemIsolation filesystem,
        NetworkIsolation network,
        ProcessIsolation process
) {

    public EnvironmentIsolation {
        filesystem = filesystem != null ? filesystem : FilesystemIsolation.NONE;
        network = network != null ? network : NetworkIsolation.NONE;
        process = process != null ? process : ProcessIsolation.NONE;
    }

    public static EnvironmentIsolation unconstrained() {
        return new EnvironmentIsolation(
                FilesystemIsolation.NONE,
                NetworkIsolation.NONE,
                ProcessIsolation.NONE
        );
    }

    public static EnvironmentIsolation sandboxed() {
        return new EnvironmentIsolation(
                FilesystemIsolation.SANDBOX,
                NetworkIsolation.LOOPBACK_ONLY,
                ProcessIsolation.SANDBOXED_PROCESS
        );
    }
}
