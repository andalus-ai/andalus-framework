package tech.kayys.wayang.harness.environment.v3.process;

import java.nio.file.Path;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Request describing a process to be executed in the environment.
 */
public record ProcessRequest(
        String command,
        List<String> arguments,
        Map<String, String> environmentVariables,
        Path workingDirectory,
        Duration timeout
) {
    public ProcessRequest {
        Objects.requireNonNull(command, "command");
        arguments = arguments != null ? List.copyOf(arguments) : List.of();
        environmentVariables = environmentVariables != null ? Map.copyOf(environmentVariables) : Map.of();
        timeout = timeout != null ? timeout : Duration.ofMinutes(1);
    }

    public static ProcessRequest of(String command, String... args) {
        return new ProcessRequest(command, List.of(args), Map.of(), null, Duration.ofMinutes(1));
    }
}
