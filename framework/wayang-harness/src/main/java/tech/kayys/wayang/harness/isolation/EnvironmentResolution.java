package tech.kayys.wayang.harness.isolation;

import java.util.Objects;
import java.util.Optional;

/**
 * Result of resolving a matching execution environment and resource allocation.
 */
public record EnvironmentResolution(
        boolean satisfied,
        Optional<ExecutionEnvironment> environment,
        Optional<ResourceAllocation> allocation,
        String message
) {

    public EnvironmentResolution {
        environment = environment != null ? environment : Optional.empty();
        allocation = allocation != null ? allocation : Optional.empty();
        message = message != null ? message : "";
    }

    public static EnvironmentResolution success(ExecutionEnvironment env, ResourceAllocation alloc) {
        return new EnvironmentResolution(
                true,
                Optional.of(Objects.requireNonNull(env)),
                Optional.of(Objects.requireNonNull(alloc)),
                "Environment resolved successfully"
        );
    }

    public static EnvironmentResolution failure(String reason) {
        return new EnvironmentResolution(false, Optional.empty(), Optional.empty(), reason);
    }
}
