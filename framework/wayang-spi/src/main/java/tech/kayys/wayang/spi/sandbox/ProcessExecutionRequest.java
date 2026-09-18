package tech.kayys.wayang.spi.sandbox;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public record ProcessExecutionRequest(
        List<String> command,
        Map<String, String> environment,
        Duration timeout,
        boolean inheritEnvironment
) {

    public ProcessExecutionRequest {
        command = List.copyOf(
                Objects.requireNonNull(command, "command"));

        if (command.isEmpty()) {
            throw new IllegalArgumentException(
                    "command must not be empty");
        }

        environment = environment == null
                ? Map.of()
                : Map.copyOf(environment);

        timeout = Objects.requireNonNull(timeout, "timeout");

        if (timeout.isZero() || timeout.isNegative()) {
            throw new IllegalArgumentException(
                    "timeout must be positive");
        }
    }

    public static ProcessExecutionRequest of(
            List<String> command,
            Duration timeout) {

        return new ProcessExecutionRequest(
                command,
                Map.of(),
                timeout,
                false
        );
    }
}
