package tech.kayys.wayang.harness.isolation;

import tech.kayys.wayang.harness.semantics.Operation;

import java.time.Duration;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Standard reference implementation of {@link EnvironmentResolver}.
 */
public class DefaultEnvironmentResolver implements EnvironmentResolver {

    @Override
    public EnvironmentResolution resolve(
            Operation operation,
            ResourceRequest resources,
            List<ExecutionEnvironment> candidateEnvironments
    ) {
        Objects.requireNonNull(operation, "Operation cannot be null");
        resources = resources != null ? resources : ResourceRequest.minimal();
        if (candidateEnvironments == null || candidateEnvironments.isEmpty()) {
            return EnvironmentResolution.failure("No candidate environments available");
        }

        boolean needsSandbox = operation.constraints().requireSandbox();
        boolean needsGpu = resources.requirements().stream().anyMatch(r -> r.type() == ResourceType.GPU);

        Optional<ExecutionEnvironment> matched = candidateEnvironments.stream()
                .filter(env -> {
                    if (needsSandbox) {
                        boolean isIsolated = env.type() == EnvironmentType.SANDBOX
                                || env.type() == EnvironmentType.CONTAINER
                                || env.type() == EnvironmentType.VM;
                        if (!isIsolated) {
                            return false;
                        }
                    }
                    if (needsGpu && !env.capabilities().supportsGpu()) {
                        return false;
                    }
                    return true;
                })
                .findFirst();

        if (matched.isEmpty()) {
            return EnvironmentResolution.failure("No matching environment satisfies the isolation and resource constraints");
        }

        ExecutionEnvironment env = matched.get();
        DefaultResourceAllocation allocation = DefaultResourceAllocation.allocate(
                env.id(),
                resources.requirements(),
                Duration.ofMinutes(30)
        );

        return EnvironmentResolution.success(env, allocation);
    }
}
