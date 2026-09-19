package tech.kayys.wayang.execution.process;

import java.util.List;
import java.util.Objects;

/**
 * Structured request to execute a process inside a sandbox.
 */
public record ProcessRequest(
        ExecutableReference executable,
        List<String> arguments,
        EnvironmentSpec environment,
        WorkingDirectory workingDirectory
) {

    public ProcessRequest {
        Objects.requireNonNull(executable, "executable cannot be null");
        arguments = arguments != null ? List.copyOf(arguments) : List.of();
        environment = environment != null ? environment : EnvironmentSpec.empty();
        workingDirectory = workingDirectory != null ? workingDirectory : WorkingDirectory.workspaceRoot();
    }

    public static ProcessRequest of(String binary, List<String> arguments) {
        return new ProcessRequest(
                ExecutableReference.of(binary),
                arguments,
                EnvironmentSpec.empty(),
                WorkingDirectory.workspaceRoot()
        );
    }
}
