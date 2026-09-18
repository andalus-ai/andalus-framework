package tech.kayys.wayang.spi.sandbox.container;

import java.time.Duration;
import java.util.Objects;

public record ContainerExecution(
        int exitCode,
        String stdout,
        String stderr,
        Duration duration,
        boolean timedOut
) {
    public ContainerExecution {
        stdout = Objects.requireNonNullElse(stdout, "");
        stderr = Objects.requireNonNullElse(stderr, "");
        duration = Objects.requireNonNull(duration, "duration");
    }

    public boolean successful() {
        return !timedOut && exitCode == 0;
    }
}
