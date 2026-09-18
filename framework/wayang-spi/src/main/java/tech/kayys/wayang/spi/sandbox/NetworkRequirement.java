package tech.kayys.wayang.spi.sandbox;

import java.util.Objects;

public record NetworkRequirement(
        NetworkMode mode,
        boolean requiresDns,
        boolean requiresLoopback
) {

    public NetworkRequirement {
        mode = Objects.requireNonNull(
                mode,
                "mode");
    }

    public boolean requiresIsolation() {
        return mode != NetworkMode.FULL;
    }
}
