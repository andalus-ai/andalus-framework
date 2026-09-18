package tech.kayys.wayang.spi.sandbox;

import java.time.Duration;
import java.util.Objects;
import java.util.Optional;

public record ProcessExecutionResult(
        int exitCode,
        String stdout,
        String stderr,
        Duration duration,
        boolean timedOut,
        boolean forciblyTerminated,
        boolean outputLimitExceeded
) {

    public ProcessExecutionResult(
            int exitCode,
            String stdout,
            String stderr,
            Duration duration,
            boolean timedOut,
            boolean forciblyTerminated) {
        this(exitCode, stdout, stderr, duration, timedOut, forciblyTerminated, false);
    }

    public ProcessExecutionResult {
        stdout = Objects.requireNonNullElse(stdout, "");
        stderr = Objects.requireNonNullElse(stderr, "");
        duration = Objects.requireNonNull(duration, "duration");
    }

    public boolean successful() {
        return !timedOut
                && !forciblyTerminated
                && !outputLimitExceeded
                && exitCode == 0;
    }

    public Optional<Integer> exitCodeOptional() {
        return exitCode >= 0
                ? Optional.of(exitCode)
                : Optional.empty();
    }
}
