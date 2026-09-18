package tech.kayys.wayang.spi.sandbox.container;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public record ContainerExecutionRequest(
        List<String> command,
        Map<String, String> environment,
        Duration timeout,
        boolean interactive
) {

    public ContainerExecutionRequest {
        command = command == null
                ? List.of()
                : List.copyOf(command);

        if (command.isEmpty()) {
            throw new IllegalArgumentException(
                    "command must not be empty");
        }

        environment = environment == null
                ? Map.of()
                : Map.copyOf(environment);

        timeout = Objects.requireNonNull(
                timeout,
                "timeout");

        if (timeout.isNegative()
                || timeout.isZero()) {

            throw new IllegalArgumentException(
                    "timeout must be positive");
        }
    }
}
